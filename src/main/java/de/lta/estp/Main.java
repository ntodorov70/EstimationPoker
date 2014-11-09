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

import com.hazelcast.core.*;
import de.lta.estp.data.BidEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Main {
	private JFrame frame;
	private JPanel contentPanel;
	private JLabel title;
	private MainTable table;
	private MainTableModel   tableModel;

    private HazelcastInstance hazelcast;
    private com.hazelcast.core.Member myMember;
    private IMap<Long, BidEntry> bidEntries;
    private IMap<Long, de.lta.estp.data.Member> members;
    private IMap<String,Double> offers;
    de.lta.estp.data.Member currentMember;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
            try {
                Main window = new Main();
                window.initialize();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
			}
		});
	}

	private void initializeHC(){
        hazelcast= Hazelcast.newHazelcastInstance(null);
        myMember = hazelcast.getCluster().getLocalMember();
        bidEntries = hazelcast.getMap("estpApp-bidEntries");
        members = hazelcast.getMap("estpApp-members");
        offers =  hazelcast.getMap("estpApp-offers");

        bidEntries.addEntryListener(new BidMapListener(), true);
        members.addEntryListener(new BidersListener(), true);
        offers.addEntryListener(new OfferMapListener(), true);
    }

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100,100, 450, 300);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                currentMember.setOnline(false);
                members.put(currentMember.getId(), currentMember);
                System.exit(0);
            }
        });
		contentPanel = new JPanel(new BorderLayout(0,0));
		frame.getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		Box titleBox = Box.createVerticalBox();
		
		titleBox.add(title  = new JLabel());
		contentPanel.add(titleBox,BorderLayout.NORTH);
		
		table = new MainTable();
		table.setModel(tableModel = new MainTableModel());
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

		contentPanel.add(new JScrollPane(table),BorderLayout.CENTER);
		
		// build menu
		JMenu exportMenu = new JMenu("File");
		JMenuItem openMenu = new JMenuItem( "New bid entry" );
		openMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
                Long id = System.currentTimeMillis();
                String entryName = ""+ (bidEntries.size());
                bidEntries.put(id, new BidEntry(id,entryName));
			}
		});
		exportMenu.add(openMenu);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(exportMenu);
		frame.setJMenuBar(menuBar);

        initializeHC();
        currentMember = addCurrentBidder();
        tableModel.useData(currentMember, members, bidEntries, offers);
	}

    de.lta.estp.data.Member addCurrentBidder(){
        // assure the unique currentBidder id of every member
        String userName = getUniqueBidderNameFor(System.getProperty("user.name"));
        Long id = System.currentTimeMillis();
        de.lta.estp.data.Member newBider = new de.lta.estp.data.Member(id, userName);
        members.put(id, newBider);

        frame.setTitle(userName);
        return newBider;
    }

    String getUniqueBidderNameFor(String userName){
        int uniqueSuffix = 0;
        String newUserName = userName;
        while(bidderNameInUse(newUserName)){
            newUserName = userName + " ("+(uniqueSuffix++)+")";
        }
        return newUserName;
    }

    boolean bidderNameInUse(String biderName){
        for (de.lta.estp.data.Member bider : members.values()) {
            if(bider.getName().equals(biderName))
                return true;
        }
        return false;
    }

	String createObjectInfoPanel(Object value){
        return value.getClass().getCanonicalName();
	}
	

    private class BidMapListener implements EntryListener<Long,BidEntry>{

        @Override
        public void entryAdded(EntryEvent<Long,BidEntry> entryEvent) {
            final BidEntry entry = entryEvent.getValue();
            //tableModel.updateCatalogue();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tableModel.update();
                    //tableModel.updateBidEntry(entry);
                }
            });

        }

        @Override
        public void entryRemoved(EntryEvent<Long,BidEntry> entryEvent) {
            BidEntry entry = entryEvent.getValue();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tableModel.update();
                }
            });
        }

        @Override
        public void entryUpdated(EntryEvent<Long,BidEntry> entryEvent) {
            final BidEntry entry = entryEvent.getValue();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tableModel.updateBidEntry(entry);
                }
            });
        }

        @Override
        public void entryEvicted(EntryEvent entryEvent) {

        }

        @Override
        public void mapEvicted(MapEvent mapEvent) {

        }

        @Override
        public void mapCleared(MapEvent mapEvent) {

        }
    }
    /**
     * Notifies entry changes
     */
    private class BidersListener implements EntryListener<Long, de.lta.estp.data.Member> {

        @Override
        public void entryAdded(EntryEvent<Long, de.lta.estp.data.Member> event) {
            de.lta.estp.data.Member addedMember = event.getValue();
            if(!currentMember.equals(addedMember))
                tableModel.addBidder(addedMember);
        }

        @Override
        public void entryRemoved(EntryEvent<Long, de.lta.estp.data.Member> event) {
            throw new RuntimeException("Biders can not be removed");
        }

        @Override
        public void entryUpdated(EntryEvent<Long, de.lta.estp.data.Member> event) {
            de.lta.estp.data.Member item = event.getValue();
            String bidderID = item.getName();
            tableModel.update();
        }

        @Override
        public void entryEvicted(EntryEvent<Long, de.lta.estp.data.Member> longBiderEntryEvent) {

        }

        @Override
        public void mapEvicted(MapEvent mapEvent) {

        }

        @Override
        public void mapCleared(MapEvent mapEvent) {

        }
    }

    private class OfferMapListener implements EntryListener<String, Double> {
        @Override
        public void entryAdded(EntryEvent<String, Double> entryEvent) {
            final String offer_key = entryEvent.getKey();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tableModel.updateOffer(offer_key);
                    //tableModel.update();
                }
            });
        }

        @Override
        public void entryRemoved(EntryEvent<String, Double> entryEvent) {
            final String offer_key = entryEvent.getKey();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tableModel.updateOffer(offer_key);
                    tableModel.update();
                }
            });
        }

        @Override
        public void entryUpdated(EntryEvent<String, Double> entryEvent) {
            final String offer_key = entryEvent.getKey();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tableModel.updateOffer(offer_key);
                }
            });
        }

        @Override
        public void entryEvicted(EntryEvent<String, Double> stringDoubleEntryEvent) {

        }

        @Override
        public void mapEvicted(MapEvent mapEvent) {

        }

        @Override
        public void mapCleared(MapEvent mapEvent) {

        }
    }
}
