package translation;

import org.json.JSONArray;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


// TODO Task D: Update the GUI for the program to align with UI shown in the README example.
//            Currently, the program only uses the CanadaTranslator and the user has
//            to manually enter the language code they want to use for the translation.
//            See the examples package for some code snippets that may be useful when updating
//            the GUI.
public class GUI {

    private static final List<String> CountryNames = new ArrayList<>();
    private static String selectedLanguage;
    private static String selectedCountry;
    private static void GetCountryNames() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(GUI.class
                    .getClassLoader().getResource("country-codes.txt").toURI()));

            Iterator<String> iterator = lines.iterator();
            iterator.next(); // skip the first line
            while (iterator.hasNext()) {
                String line = iterator.next();
                String regex = "[,\\.\\s]";
                String[] splitted = line.split(regex);
                CountryNames.add(splitted[0]);
            }
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GetCountryNames();
            JList<String> list = new JList<>(CountryNames.toArray(new String[0]));
            list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            list.setLayoutOrientation(JList.VERTICAL);
            list.setVisibleRowCount(-1);
            JScrollPane listScroller = new JScrollPane(list);
            listScroller.setPreferredSize(new Dimension(250, 80));
//          JPanel countryPanel = new JPanel();
            JTextField countryField = new JTextField(10);
            listScroller.add(new JLabel("Country:"));
            listScroller.add(countryField);

            JPanel languagePanel = new JPanel();
            JTextField languageField = new JTextField(10);
            languagePanel.add(new JLabel("Language:"));
            //languagePanel.add(languageField);

            //create a combobox to support language selection
            Translator translator = new JSONTranslator();
            LanguageCodeConverter converter = new LanguageCodeConverter();
            JComboBox<String> languageComboBox = new JComboBox<>();
            for(String languageCode : translator.getLanguageCodes()) {
                //call a method which reads each language from a language code
                // and add it to the combobox
                languageComboBox.addItem(converter.fromLanguageCode(languageCode));

            }
            languagePanel.add(languageComboBox);

            JPanel buttonPanel = new JPanel();
            JButton submit = new JButton("Submit");
            buttonPanel.add(submit);

            JLabel resultLabelText = new JLabel("Translation:");
            buttonPanel.add(resultLabelText);
            JLabel resultLabel = new JLabel("\t\t\t\t\t\t\t");
            buttonPanel.add(resultLabel);

            languageComboBox.addItemListener(new ItemListener() {

                /**
                 * Invoked when an item has been selected or deselected by the user.
                 * The code written for this method performs the operations
                 * that need to occur when an item is selected (or deselected).
                 *
                 * @param e the event to be processed
                 */
                @Override
                public void itemStateChanged(ItemEvent e) {

                    if (e.getStateChange() == ItemEvent.SELECTED) {
                         selectedLanguage = languageComboBox.getSelectedItem().toString();
                    }
                }


            });

            list.addListSelectionListener(new ListSelectionListener() {

                /**
                 * Called whenever the value of the selection changes.
                 *
                 * @param e the event that characterizes the change.
                 */
                @Override
                public void valueChanged(ListSelectionEvent e) {

                    int[] indices = list.getSelectedIndices();
                    String[] items = new String[indices.length];
                    for (int i = 0; i < indices.length; i++) {
                        items[i] = list.getModel().getElementAt(indices[i]);
                    }
                    selectedCountry = items[0];

                }
            });

            // adding listener for when the user clicks the submit button
            submit.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                   // String language = languageField.getText();
                    // String country = countryField.getText();

                    // for now, just using our simple translator, but
                    // we'll need to use the real JSON version later.
                    Translator translator = new JSONTranslator();
                    CountryCodeConverter countryConverter = new CountryCodeConverter();
                    LanguageCodeConverter languageConverter = new LanguageCodeConverter();

                    String selectedCountryCode = countryConverter.fromCountry(selectedCountry);
                    String selectedLanguageCode = languageConverter.fromLanguage(selectedLanguage);


                    String result = translator.translate(selectedCountryCode.toLowerCase(), selectedLanguageCode);
                    if (result == null) {
                        result = "no translation found!";
                    }
                    resultLabel.setText(result);

                }

            });

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            //mainPanel.add(countryPanel);
            mainPanel.add(languagePanel);
            mainPanel.add(listScroller);
            mainPanel.add(buttonPanel);


            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);


        });
    }
}
