/*
 * Copyright 2013 Nikolay Todorov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.lta.estp;

import com.hazelcast.core.IMap;
import de.lta.estp.data.BidEntry;
import de.lta.estp.data.BidEntryInfo;
import de.lta.estp.data.Member;
import de.lta.util.swing.table.GenTableModel;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;


public class MainTableModel extends GenTableModel<BidEntry> {
	private static final long serialVersionUID = 4364650213927043699L;

	public static String COL_NAME = "COL_NAME";
	public static String COL_AVERAGE = "COL_AVERAGE";

    Member currentMember;
    IMap<Long, Member> members;
    IMap<Long,BidEntry> bidEntries;
    Map<Long, BidEntryInfo> bidEntryInfoMap;
    IMap<String, Double> offers;
    DataProviderMap dataProvider = new DataProviderMap();
    NumberFormat format = NumberFormat.getCurrencyInstance();

	public MainTableModel(){
        createColumns();
	}


    @Override
    public AbstractDataProvider getDataProvider() {
        return dataProvider;
    }

	protected void createColumns(){

		Column nameColumn = new Column(COL_NAME, "Bid Item", String.class ,true){
			@Override
			public Object readValue(BidEntry entry) {

                return entry.getName();
			}

			@Override
			public void writeValue(BidEntry modelEntry, Object value) {
                String newName = "";
				if(value != null)
                    newName = value.toString();
				modelEntry.setName(newName);
                bidEntries.put(modelEntry.getId(),modelEntry);
			}
		};


		Column sumColumn = new Column(COL_AVERAGE, "Average", Double.class, false){
			@Override
			public Object readValue(BidEntry entry) {
                if(getBidEntryInfo(entry).isEstimationComplete())
                    return getBidEntryInfo(entry).getAverage();
                else
                    return null;
			}
		};

		addColumn(nameColumn);
		addColumn(sumColumn);
	}

    public void useData(Member currentMember,  IMap<Long,Member> members, IMap<Long, BidEntry> bidEntries, final IMap<String, Double> offers) {
        this.currentMember = currentMember;
        this.bidEntries = bidEntries;
        this.members = members;
        this.offers = offers;
        bidEntryInfoMap = new HashMap<Long, BidEntryInfo>();

        for (Long bidderID : new TreeSet<Long>( members.keySet())) {
            addBidder(members.get(bidderID));
        }

        for (BidEntry bidEntry : bidEntries.values()) {
            refreshBidEntry(bidEntry);
        }

        dataProvider.useData(bidEntries);
    }

    public void addBidder(final Member member){
        final boolean colForCurrentMember = member.equals(currentMember);

        Column userColumn = new Column(member.getId(), member.getName(), Double.class, colForCurrentMember){

            @Override
            public Object readValue(BidEntry entry) {
                if(colForCurrentMember || getBidEntryInfo(entry
                ).isEstimationComplete()) {
                    String offer_key = getOfferID(entry, member);
                    return offers.get(offer_key);
                }else{
                    return null;
                }
            }

            @Override
            public void writeValue(BidEntry entry, Object value) {
                String offer_key = getOfferID(entry,member);
                if(value==null)
                    offers.remove(offer_key);
                else {
                    offers.put(offer_key, (Double) value);
                }
            }

        };

        addColumn(userColumn);
    }

    public void update() {
        updateCatalogue();
        fireTableDataChanged();
    }

    public void updateCatalogue(){
        dataProvider.updateCatalogue();
    }

    public void updateBidEntry(BidEntry entry) {
        int row = dataProvider.getRowForKey(entry.getId());
        refreshBidEntry(entry);
        int col = 0;
        fireTableRowsUpdated(row,row);
        //fireTableCellUpdated(row-1, col-1);
    }

    /**
     * Update table on new or updated offer.
     *
     * @param offerKey
     */
    public void updateOffer(String offerKey) {
        BidEntry entry = getBidEntryByOfferKey(offerKey);
        refreshBidEntry(entry);
        int row = dataProvider.getRowForKey(entry.getId());
        int col = 0;
        fireTableRowsUpdated(row,row);
    }

    String getOfferID( BidEntry entry, Member member){
        return entry.getId()+"-" + member.getId();
    }

    BidEntry getBidEntryByOfferKey(String offerKey){
        String entrykeyStr = offerKey.split("-")[0];
        return bidEntries.get(Long.parseLong(entrykeyStr));
    }

    /**
     * Checks if estimation is complete (offer from all members available).
     * Calculates the average offer.
     */
    public void refreshBidEntry(BidEntry entry){
        Long bidEntryID = entry.getId();
        Double sum = 0d;
        int offerCount = 0;
        boolean estimationComplete = true;
        for (Member member : members.values()) {
            Object offerID = getOfferID(entry,member);
            Double offer = offers.get(offerID);
            if(offer!=null){
                sum+=offer;
                offerCount++;
            }else{
                if(member.isOnline()) { // there is at least one memmber without offer
                    estimationComplete = false;
                }
            }
        }
        BidEntryInfo entryInfo = getBidEntryInfo(entry);

        entryInfo.setAverage(offerCount>0?sum/offerCount:null);
        entryInfo.setEstimationComplete(estimationComplete);
    }

    private BidEntryInfo getBidEntryInfo(BidEntry entry) {
        BidEntryInfo entryInfo = bidEntryInfoMap.get(entry.getId());
        if(entryInfo == null){
            entryInfo = new BidEntryInfo();
            bidEntryInfoMap.put(entry.getId(),entryInfo);
        }
        return entryInfo;
    }
}
