
import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class PUMLConverter {
    public static void seqToState(String path,String fileName) throws IOException {
        StringBuilder output = new StringBuilder("@startuml\n").append("[*] --> Idle \n").append("Idle --> ");  // Default ausgabe für jeder puml Datei
        ArrayList<State> zustande = new ArrayList<>();
        ArrayList<String> LifeLines = new ArrayList<>();
        int i = 0; // Counter alle Zeilen
        int k = 0; // State Counter
        int j = 1; // SubState Counter
        int lc =0; // loop Counter
        boolean notInLoop = true;
        String condition="";
        String pfeiltyp;

        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            String[] zeilen;
            String[] zeilen2;

            while (myReader.hasNextLine()) { // Über die linesListe iterieren
                String line = myReader.nextLine();
                line = line.replaceAll("\\s+", ""); // alle Sonder-/leerzeichen löschen
                pfeiltyp = pfeilDelimiter(line);
                i++;
                if (line.contains("loop") || line.contains("opt")) {
                    if (line.contains("loop")) {
                        zeilen = line.split("loop", 2);
                        condition = zeilen[1];
                    }
                    if (line.contains("opt")) {
                        zeilen = line.split("opt", 2);
                        condition = zeilen[1];
                    }

                    //Fügt den letzten Zustand hinzu, bevor die Schleife gestartet wird
                    if (!(k == 0)) {
                        State lastState = new State(i, "S" + (k + 1), zustande.listIterator(k).previous().quelle, zustande.listIterator(k).previous().ziel, zustande.listIterator(k).previous().sendMessage, "");
                        zustande.add(lastState);
                        output.append(zustande.listIterator(k).previous().name).append(" --> ").append(lastState.name).append(" : ");
                        output.append(lastState.recievedMessage).append("/").append(lastState.sendMessage).append("\n");
                        k++;
                    }
                    notInLoop = false;
                    lc++;
                } else {

                    if ((line.contains("->") || (line.contains("<-"))) && (notInLoop)&&(line.contains(":"))) { //State außerhalb der Loop bearbeiten
                        zeilen = line.split(pfeiltyp, 2);
                        zeilen2 = zeilen[1].split(":", 2);
                        j = 1;
                        if (!(zeilen[0].equals(zeilen2[0]))) {
                            //Wenn der erste Status zwischen 2 verschiedenen Zeitleisten verbunden ist, --> Ein Status für die Lifeline wird hinzugefügt
                            if (LifeLines.isEmpty()) {
                                LifeLines.add(zeilen[0]);
                                LifeLines.add(zeilen2[0]);
                                output.append(zeilen2[0]).append(" : ").append(zeilen2[1]).append("\n");
                                output.append("state ").append(zeilen2[0]).append("{").append("\n");
                                continue;
                            }
                            LifelinesChecker(output, LifeLines, zeilen2);
                        }

                        //Überprüft, ob es sich um den ersten Status handelt → fügt die geeignete Form von Daten zu unserer Puml-Ausgabe hinzu
                        if (!zustande.listIterator(k).hasPrevious()) {
                            State added = new State(i, "S" + (k + 1), zeilen[0], zeilen2[0], "", zeilen2[1]);
                            zustande.add(added);
                            output.append("[*]").append(" --> ").append("Idle").append(k).append("\n");
                            output.append("Idle").append(k).append(" -->").append(added.name);
                            output.append(" : ").append(added.sendMessage).append("\n");

                        }
                        //Überprüft, ob es sich nicht um den ersten oder den letzten Status handelt → fügt die geeignete Form von Daten zu unserer Puml-Ausgabe hinzu
                        if ((zustande.listIterator(k).hasPrevious()) && (zustande.listIterator(k - 1).hasNext())) {

                            State newState = new State(i, "S" + (k + 1), zeilen[0], zeilen2[0], zustande.listIterator(k).previous().sendMessage, zeilen2[1]);
                            zustande.add(newState);
                            output.append(zustande.listIterator(k).previous().name).append(" --> ").append(newState.name).append(" : ");
                            if (!condition.equals("")) {
                                output.append("!(").append(condition).append(")\n");
                                condition = "";
                            }else {
                                output.append(newState.recievedMessage).append("/").append(newState.sendMessage).append("\n");
                            }
                        }
                        //Überprüft, ob es sich um den letzten Status handelt → fügt die geeignete Form von Daten zu unserer Puml-Ausgabe hinzu
                        if (!zustande.listIterator(k).hasNext()) {
                            State lastState = new State(i, "S" + (k + 1), zeilen[0], zeilen2[0], zustande.listIterator(k).previous().sendMessage, "");
                            zustande.add(lastState);
                            output.append(zustande.listIterator(k).previous().name).append(" --> ").append(lastState.name).append(" : ");
                            output.append(lastState.recievedMessage).append("/").append(lastState.sendMessage).append("\n");

                        }
                        k++;
                    }


                    if ((line.contains("->") || (line.contains("<-"))) && (!notInLoop)) { //States innerhalb der Loop bearbeiten
                        zeilen = line.split(pfeiltyp, 2);
                        zeilen2 = zeilen[1].split(":", 2);
                        if (k == 0) {
                            if (!(zeilen[0].equals(zeilen2[0]))) {
                                if (LifeLines.isEmpty()) {
                                    LifeLines.add(zeilen[0]);
                                    LifeLines.add(zeilen2[0]);
                                    output.append(zeilen2[0]).append("\n");
                                    output.append("state ").append(zeilen2[0]).append("{").append("\n");
                                }else {
                                    LifelinesChecker(output, LifeLines, zeilen2);
                                }
                            }
                            State added = new State(i, "S" + (k + 1), zeilen[0], zeilen2[0], "", zeilen2[1]);
                            zustande.add(added);
                            output.append("[*]").append(" --> ").append("Idle").append(k).append("\n");
                            output.append("Idle").append(k).append(" -->").append(added.name);
                            output.append(" : ").append(added.sendMessage).append("\n");
                            k++;
                        }

                        //Ein neuer Sub-zustand wird hinzugefügt, der den Status in der Schleife enthält
                        output.append("state S").append(k).append("{").append("\n");
                        output.append("S").append(k).append(" --> ").append("S").append(k).append(" : ").append(condition).append("\n");
                        State subZustandList = new State(i, "S" + k, zeilen[0], zeilen2[0], zeilen2[1], "", new ArrayList<>());
                        State zustand = new State(i, subZustandList.name + "." + j, zeilen[0], zeilen2[0], "", zeilen2[1]);
                        subZustandList.subState.add(zustand);
                        //zustande.add(subZustandList);

                        //Sowohl der Idle-zustand als auch der Erstzustand werden hier zuerst hinzugefügt und verbunden
                        output.append("[*] --> Idle.S").append(k).append("\n");
                        output.append("Idle.S").append(k).append(" -->").append(zustand.name);
                        output.append(" : ").append(zustand.sendMessage).append("\n");
                        int temp = k;
                        //k++;
                        do {
                            j++;
                            line = myReader.nextLine();
                            if (line.contains("loop")) { // Überprüft, ob eine verschachtelte Schleife vorhanden ist
                                lc++;
                                temp++;
                                output.append("state S").append(temp).append("{").append("\n"); //Sowohl der Idle-zustand als auch der Erstzustand werden hier zuerst hinzugefügt und verbunden
                                line = myReader.nextLine();
                            }

                            if (line.contains("end") && (!line.contains("@enduml"))) {
                                State lastZustand = new State(i, "s" + temp + "." + j, subZustandList.subState.listIterator(j - 1).previous().quelle, subZustandList.subState.listIterator(j - 1).previous().ziel, subZustandList.subState.listIterator(j - 1).previous().sendMessage, "");
                                subZustandList.subState.add(lastZustand);
                                output.append(subZustandList.subState.listIterator(j - 1).previous().name).append(" --> ").append(lastZustand.name).append(" : ");
                                output.append(lastZustand.recievedMessage).append("/").append(lastZustand.sendMessage).append("\n");
                                output.append(lastZustand.name).append(" --> ").append("[*]").append("\n");
                                lc--;
                                output.append("}" + "\n");
                                if (lc == 0) {
                                    notInLoop = true;
                                    break;
                                } else {
                                    line = myReader.nextLine();
                                }
                            }
                            String[] lZeilen = line.split(pfeiltyp, 2);
                            String[] lZeilen2 = lZeilen[1].split(":", 2);

                            State newZustand = new State(i, "S" + temp + "." + j, lZeilen[0], lZeilen2[0], subZustandList.subState.listIterator(j - 1).previous().sendMessage, lZeilen2[1]);
                            subZustandList.subState.add(newZustand);
                            output.append(subZustandList.subState.listIterator(j - 1).previous().name).append(" --> ").append(newZustand.name).append(" : ");
                            output.append(newZustand.recievedMessage).append("/").append(newZustand.sendMessage).append("\n");

                        } while (true);
                    }

                }

            }
            //Verbindet den letzten Zustand mit dem Endstatus
            State lastState = new State(i, "S" + (k+1), zustande.listIterator(k).previous().quelle, zustande.listIterator(k).previous().ziel, zustande.listIterator(k).previous().sendMessage,"");
            zustande.add(lastState);
            output.append(zustande.listIterator(k).previous().name).append(" --> ").append(lastState.name).append(" : ");
            if (!condition.equals("")){
                output.append("!(").append(condition).append(")\n");
            }else {
                output.append(lastState.recievedMessage).append("/").append(lastState.sendMessage).append("\n");
            }
            output.append(lastState.name).append(" --> ").append("[*]").append("\n");
            output.append("@enduml");
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(zustande.toArray())); // Inhalt der Arraylist<zustande> anzeigen
        System.out.println(i);

        // Extension .puml von FileName entfernen
        String[] stateFileName = fileName.split("puml",2);
        String sfn = stateFileName[0];

        File file = new File("ZustandDiagramm");

        boolean dirCreated = file.mkdir();

        // Das generierte Zustandsdiagramm als PNG mit demselben Namen wie die PUML Datei speichern
        OutputStream png = new FileOutputStream("ZustandDiagramm/"+sfn+"png");


        SourceStringReader reader = new SourceStringReader(output.toString());
        // Write the first image to "png"
        String desc = reader.outputImage(png).getDescription();
        // Return a null string if no generation

        //generate a txt File for the Output
        FileWriter fw = new FileWriter("ZustandDiagramm/"+sfn+"txt");
        fw.write(output.toString());
        fw.flush();
        fw.close();

    }

    /**
     *Diese Methode prüft, ob der Lifeline schon bereit in der Lifelines-Arraylist existiert
     *
     * @param Lifeline Lifeline zu prüfen
     * @param lifeLineList Arraylist, die alle Lifelines enthält
     * @param output
     */
    private static void LifelinesChecker(StringBuilder output, ArrayList<String> lifeLineList, String[] Lifeline) {
        for (String s: lifeLineList) {
            boolean checker=false;
            for (String s2: lifeLineList) {
                if (s.equals(s2)) {
                    checker = true;
                    break;
                }
            }
            if (!checker){
                output.append("state ").append(Lifeline[0]).append("{").append("\n");
                lifeLineList.add(Lifeline[0]);
            }
        }
    }
    /**
     *Diese Methode prüft, ob der eingegebene String ein Zeiger auf links oder rechts hat und gibt es aus
     *
     * @param line String zu prüfen
     * @return Zeiger als String Form, entweder nach recht (->) oder nach links (<-)
     */
    public static String pfeilDelimiter(String line){
        String delimiter = "";
        if (line.contains("->")){
            delimiter = "->";
        }

        if(line.contains("<-")){
            delimiter = "<-";
        }

        return delimiter;
    }


}

