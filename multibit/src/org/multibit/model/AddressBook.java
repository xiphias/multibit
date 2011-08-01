package org.multibit.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.google.bitcoin.core.Address;

public class AddressBook {

    private SortedSet<AddressBookData> receivingAddresses;
    private SortedSet<AddressBookData> sendingAddresses;

    private String ADDRESS_BOOK_FILENAME = "multibit.addressbook";
    private String RECEIVE_ADDRESS_MARKER = "receive";
    private String SEND_ADDRESS_MARKER = "send";
    private String SEPARATOR = ",";

    public AddressBook() {
        receivingAddresses = new TreeSet<AddressBookData>();
        sendingAddresses = new TreeSet<AddressBookData>();

        loadFromFile();
        this.createFakeReceivingAddressBookData();
        this.createFakeSendingAddressBookData();
    }

    public SortedSet<AddressBookData> getReceivingAddresses() {
        return receivingAddresses;
    }

    public void setReceivingAddresses(SortedSet<AddressBookData> receivingAddresses) {
        this.receivingAddresses = receivingAddresses;
    }

    public SortedSet<AddressBookData> getSendingAddresses() {
        return sendingAddresses;
    }

    public void setSendingAddresses(SortedSet<AddressBookData> sendingAddresses) {
        this.sendingAddresses = sendingAddresses;
    }

    private void createFakeReceivingAddressBookData() {

        // fake data
        AddressBookData addressBookData = new AddressBookData("Mt Gox account",
                "1GFrHq4CC8E5yaDARrKygX1E4yQaCUHDQZ ");
        receivingAddresses.add(addressBookData);

        addressBookData = new AddressBookData("Jim's Amazon account",
                "1GC6s72s5oiuav29p4oyBt93ZMftjTpAra ");
        receivingAddresses.add(addressBookData);

        addressBookData = new AddressBookData("Google dividend",
                "1GJwaK4Xh6QsD6u9XYqQiPEkxXuRshdeDv ");
        receivingAddresses.add(addressBookData);
    }

    private void createFakeSendingAddressBookData() {
        // fake data
        AddressBookData addressBookData = new AddressBookData("Jose Alvaro", "1x3f45ed");
        sendingAddresses.add(addressBookData);

        addressBookData = new AddressBookData("Joseph Jacks", "1xq90");
        sendingAddresses.add(addressBookData);

        addressBookData = new AddressBookData("Michelle Jones", "1j8s2");
        sendingAddresses.add(addressBookData);
    }

    /**
     * add a receiving address in the form of an AddressBookData, replacing the
     * label of any existing address
     * 
     * @param receivingAddress
     */
    public void addReceivingAddress(AddressBookData receivingAddress) {
        if (receivingAddress == null) {
            return;
        }

        boolean done = false;
        // check the address is not already in the set
        for (AddressBookData addressBookData : receivingAddresses) {
            if (addressBookData.getAddress().equals(receivingAddress.getAddress())) {
                // just update label
                addressBookData.setLabel(receivingAddress.getLabel());
                done = true;
                break;
            }
        }

        if (!done) {
            receivingAddresses.add(receivingAddress);
        }
    }

    /**
     * add a receiving address in the form of an Address, keeping the label of
     * any existing address
     * 
     * @param receivingAddress
     */
    public void addReceivingAddress(Address receivingAddress) {
        if (receivingAddress == null) {
            return;
        }

        boolean done = false;
        // check the address is not already in the set
        for (AddressBookData addressBookData : receivingAddresses) {
            if (addressBookData.getAddress().equals(receivingAddress.toString())) {
                // do nothing
                done = true;
                break;
            }
        }

        if (!done) {
            receivingAddresses.add(new AddressBookData("", receivingAddress.toString()));
        }

    }

    public void addSendingAddress(AddressBookData sendingAddress) {
        if (sendingAddress == null) {
            return;
        }

        boolean done = false;
        // check the address is not already in the set
        for (AddressBookData addressBookData : sendingAddresses) {
            if (addressBookData.getAddress().equals(sendingAddress.getAddress())) {
                // just update label
                addressBookData.setLabel(sendingAddress.getLabel());
                done = true;
                break;
            }
        }

        if (!done) {
            sendingAddresses.add(sendingAddress);
        }
    }

    public String lookupLabelForReceivingAddress(String address) {
        for (AddressBookData addressBookData : receivingAddresses) {
            if (addressBookData.getAddress().equals(address)) {
                return addressBookData.getLabel();
            }
        }

        return "";
    }

    public String lookupLabelForSendingAddress(String address) {
        for (AddressBookData addressBookData : sendingAddresses) {
            if (addressBookData.getAddress().equals(address)) {
                return addressBookData.getLabel();
            }
        }

        return "";
    }

    /**
     * write out the address book - a simple comma separated file format is used
     * - should probably be something like JSON
     */
    public void writeToFile() {
        try {
            // Create file
            FileWriter fstream = new FileWriter(ADDRESS_BOOK_FILENAME);
            BufferedWriter out = new BufferedWriter(fstream);
            for (AddressBookData addressBookData : receivingAddresses) {
                String columnOne = RECEIVE_ADDRESS_MARKER;
                String columnTwo = addressBookData.getAddress();
                String columnThree = addressBookData.getLabel();
                if (columnTwo == null) {
                    columnTwo = "";
                }
                out.write(columnOne + SEPARATOR + columnTwo + SEPARATOR + columnThree + "\n");
            }

            for (AddressBookData addressBookData : sendingAddresses) {
                String columnOne = SEND_ADDRESS_MARKER;
                String columnTwo = addressBookData.getAddress();
                String columnThree = addressBookData.getLabel();
                if (columnTwo == null) {
                    columnTwo = "";
                }
                out.write(columnOne + SEPARATOR + columnTwo + SEPARATOR + columnThree + "\n");
            }

            // Close the output stream
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void loadFromFile() {
        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fileInputStream = new FileInputStream(ADDRESS_BOOK_FILENAME);
            // Get the object of DataInputStream
            InputStream inputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            // Read File Line By Line
            while ((inputLine = bufferedReader.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(inputLine, SEPARATOR);
                int numberOfTokens = tokenizer.countTokens();
                String addressType = null;
                String address = null;
                String label = "";
                if (numberOfTokens == 2) {
                    addressType = tokenizer.nextToken();
                    address = tokenizer.nextToken();
                } else {
                    if (numberOfTokens == 3) {
                        addressType = tokenizer.nextToken();
                        address = tokenizer.nextToken();
                        label = tokenizer.nextToken();
                    }
                }
                if (RECEIVE_ADDRESS_MARKER.equals(addressType)) {
                    addReceivingAddress(new AddressBookData(label, address));
                } else {
                    if (SEND_ADDRESS_MARKER.equals(addressType)) {
                        addSendingAddress(new AddressBookData(label, address));
                    }
                }
            }
            // Close the input stream
            inputStream.close();
        } catch (Exception e) {
            // Catch exception if any
            // may well not be a file - absorb exception
        }
    }
}
