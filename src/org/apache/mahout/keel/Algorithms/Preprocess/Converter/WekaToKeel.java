/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/*
 * WekaToKeel.java
 */
package org.apache.mahout.keel.Algorithms.Preprocess.Converter;

import org.apache.mahout.keel.Dataset.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.Ostermiller.util.CSVParser;

/**
 * <p>
 * <b> WekaToKeel </b>
 * </p>
 *
 *  Clase extendida de la clase Importer. Esta clase se utiliza
 *  para leer datos localizados en ficheros con formato Weka
 * (fichero relacion-atributo) y convertirlos a formatoorg.apache.mahout.keel.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class WekaToKeel extends Importer {


    /* Metodo utilizado para convertir los datos del fichero indicado
     * mediante la variable pathnameInput a formatoorg.apache.mahout.keel.en el fichero
     * indicado por la ruta pathnameOutput
     *
     * @param pathnameInput ruta con los datos en formato Weka
     * @param pathnameOutput ruta para el fichero de datos Keel.
     *
     * @throws Exception */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {
        BufferedReader reader;
        Pattern p;
        Matcher m;
        File f;
        StringTokenizer token;
        String line = new String();
        String tokenInitial = new String();
        String nameAttribute = new String();
        String element = new String();
        String lineReduced = new String();
        String filename = "tempOf";
        int actualValueInt;
        double actualValue;
        double min;
        double max;
        int i;
        int j;
        int indexInitial = 0;
        int indexSecond = 0;
        int type = -1;


        File fileInput = new File(pathnameInput);

        filename = filename.concat(fileInput.getName());

        reader = new BufferedReader(new FileReader(pathnameInput));

        BufferedWriter auxFile = new BufferedWriter(new FileWriter(filename));

        while ((line = reader.readLine()) != null) {
            p = Pattern.compile("\\s*,\\s*");
            m = p.matcher(line);
            line = m.replaceAll(",");

            p = Pattern.compile("^\\s+");
            m = p.matcher(line);
            line = m.replaceAll("");

            p = Pattern.compile("\\s+$");
            m = p.matcher(line);
            line = m.replaceAll("");

            p = Pattern.compile("\\s+");
            m = p.matcher(line);
            line = m.replaceAll(" ");

            auxFile.write(line + "\n");
        }

        auxFile.close();
        reader.close();


        reader = new BufferedReader(new FileReader(filename));

        /* Contamos el número de atributos que existen*/
        line = reader.readLine();

        token = new StringTokenizer(line, " ");


        while (!line.equalsIgnoreCase("@data")) {
            if (line.startsWith("@")) {
                tokenInitial = token.nextToken().toLowerCase();

                if (tokenInitial.equals("@attribute")) {
                    numAttributes++;
                }
                if (tokenInitial.equals("@relation")) {
                    nameRelation = token.nextToken();

                    if (nameRelation.startsWith("'")) {
                        indexInitial = line.indexOf("\'");
                        indexSecond = line.indexOf("\'", indexInitial + 1);
                        nameRelation = line.substring(indexInitial, indexSecond + 1);
                    }

                    p = Pattern.compile("\\s+");
                    m = p.matcher(nameRelation);
                    nameRelation = m.replaceAll("");

                }

            }

            line = reader.readLine();
            token = new StringTokenizer(line, " ");
        }// end while()

        reader.close();


        /* Reservamos memoria para guardar la informacion de los atributos*/
        attribute = new Attribute[numAttributes];
        data = new Vector[numAttributes];

        for (i = 0; i < numAttributes; i++) {
            attribute[i] = new Attribute();
            data[i] = new Vector();
        }



// Insertamos la definición de los atributos en Attribute
        reader = new BufferedReader(new FileReader(filename));

        line = reader.readLine();

        i = -1;

        while (!(line.equalsIgnoreCase("@data"))) {

            if (line.startsWith("@")) {
                token = new StringTokenizer(line, " ");
                tokenInitial = token.nextToken();

                if (tokenInitial.equalsIgnoreCase("@attribute")) {
                    i++;
                    nameAttribute = token.nextToken();


                    if (nameAttribute.startsWith("'")) {
                        indexInitial = line.indexOf("\'");
                        indexSecond = line.indexOf("\'", indexInitial + 1) + 1;
                        nameAttribute = line.substring(indexInitial, indexSecond);
                    } else {
                        if (nameAttribute.contains("{")) {
                            nameAttribute = nameAttribute.substring(0, nameAttribute.indexOf("{"));
                        }
                    }

                    indexSecond = line.indexOf(nameAttribute) + nameAttribute.length();

                    nameAttribute = nameAttribute.replace("'", "");

                    p = Pattern.compile("\\s+");
                    m = p.matcher(nameAttribute);
                    nameAttribute = m.replaceAll(" ");

                    if (nameAttribute.contains(" ")) {
                        StringTokenizer tokenUcfirts = new StringTokenizer(nameAttribute, " ");
                        String lineUcfirts = "";
                        if (tokenUcfirts.hasMoreTokens()) {
                            lineUcfirts = tokenUcfirts.nextToken();
                        }
                        while (tokenUcfirts.hasMoreTokens()) {
                            lineUcfirts = lineUcfirts.concat(UcFirst(tokenUcfirts.nextToken()));
                        }

                        nameAttribute = lineUcfirts;

                    }



                    attribute[i].setName(nameAttribute);


                    lineReduced = line.substring(indexSecond + 1, line.length());

                    p = Pattern.compile("^\\s+");
                    m = p.matcher(lineReduced);
                    lineReduced = m.replaceAll("");

                    p = Pattern.compile("\\s+$");
                    m = p.matcher(lineReduced);
                    lineReduced = m.replaceAll("");


                    String lineReducedLower = lineReduced.toLowerCase();

                    if (lineReducedLower.startsWith("numeric") || lineReducedLower.startsWith("real")) {
                        attribute[i].setType(REAL);
                    } else {
                        if (lineReducedLower.startsWith("integer")) {
                            attribute[i].setType(INTEGER);
                        } else {
                            if (lineReducedLower.startsWith("string") || lineReducedLower.startsWith("date")) {
                                attribute[i].setType(NOMINAL);
                            } else {
                                attribute[i].setType(NOMINAL);

                                if (line.contains("{") && line.contains("}")) {
                                    lineReduced = line.substring(line.indexOf("{") + 1, line.indexOf("}"));

                                    p = Pattern.compile("^\\s+");
                                    m = p.matcher(lineReduced);
                                    lineReduced = m.replaceAll("");

                                    p = Pattern.compile("\\s+$");
                                    m = p.matcher(lineReduced);
                                    lineReduced = m.replaceAll("");


                                    if (lineReduced != "") {
                                        StringTokenizer listValues = new StringTokenizer(lineReduced, ",");

                                        while (listValues.hasMoreTokens()) {
                                            element = listValues.nextToken();

                                            element = element.replace("\"", "");

                                            p = Pattern.compile("[^A-ZÑa-zñ0-9_-]+");
                                            m = p.matcher(element);
                                            /**
                                             * Cambio hecho para que los nominales con espacios en blanco se dejen
                                             * con subrayado bajo "_" y sin comillas simples. Se añade la siguiente linea
                                             */
                                            element = element.replace(" ", "_");

                                            if (m.find() && !element.startsWith("'") && !element.endsWith("'") && !element.equals("?")) /**
                                             * Cambio hecho para que los nominales con espacios en blanco se dejen
                                             * con subrayado bajo "_" y sin comillas simples. Se comenta la siguiente linea
                                             */
                                            /*
                                            //element="'"+element+"'";
                                             */ {
                                                if (element.equalsIgnoreCase("<null>")) {
                                                    element = "?";
                                                }
                                            }
                                            attribute[i].addNominalValue(element);
                                        }
                                    }

                                }//end if
                            }//end else()
                        }//end else()
                    }//end else()


                    type = attribute[i].getType();

                    if (type == REAL || type == INTEGER) {
                        if (line.contains("[") && line.contains("]")) {

                            lineReduced = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

                            p = Pattern.compile("^\\s+");
                            m = p.matcher(lineReduced);
                            lineReduced = m.replaceAll("");

                            p = Pattern.compile("\\s+$");
                            m = p.matcher(lineReduced);
                            lineReduced = m.replaceAll("");


                            if (lineReduced != "") {
                                StringTokenizer range = new StringTokenizer(lineReduced, ",");

                                if (type == REAL) {
                                    attribute[i].setBounds(Double.valueOf(range.nextToken()), Double.valueOf(range.nextToken()));
                                }
                                if (type == INTEGER) {
                                    attribute[i].setBounds(Integer.valueOf(range.nextToken()), Integer.valueOf(range.nextToken()));
                                }
                            }
                        }

                    }


                }//end if

            }//end if()*/

            line = reader.readLine();

        }//end while()


        /* Almacenamos los datos en un fichero temporal para luego poder
        ser parseado con CSVParser por ','
         */
        BufferedWriter writer = new BufferedWriter(new FileWriter("temp"));
        while ((line = reader.readLine()) != null) {
            // Saltamos las líneas comentadas
            if (!line.startsWith("%") && !line.equals("\n") && !line.equals("\r") && !line.equals("")) {
                writer.write(line + "\n");
            }
        }
        writer.close();
        reader.close();

        FileReader filereader = new FileReader("temp");

        String[][] values = CSVParser.parse(filereader, ',');

        filereader.close();

        for (i = 0; i < values.length; i++) {
            for (j = 0; j < numAttributes; j++) {
                element = values[i][j];

                type = attribute[j].getType();

                p = Pattern.compile("^\\s+");
                m = p.matcher(element);
                element = m.replaceAll("");

                p = Pattern.compile("\\s+$");
                m = p.matcher(element);
                element = m.replaceAll("");

                element = element.replace("\"", "");

                if (type == NOMINAL) {
                    p = Pattern.compile("[^A-ZÑa-zñ0-9_-]+");
                    m = p.matcher(element);

                    /**
                     * Cambio hecho para que los nominales con espacios en blanco se dejen
                     * con subrayado bajo "_" y sin comillas simples. Se añade la siguiente linea
                     */
                    element = element.replace(" ", "_");

                    if (m.find() && !element.startsWith("'") && !element.endsWith("'") && !element.equals("?")) {
                        /**
                         * Cambio hecho para que los nominales con espacios en blanco se dejen
                         * con subrayado bajo "_" y sin comillas simples. Se comenta la siguiente linea
                         */
                        /*
                        //element="'"+element+"'";
                         */
                    }


                }

                if (element.equalsIgnoreCase("<null>")) {
                    element = "?";
                }
                data[j].addElement(element);

            }
        }


        /* Recogemos la lista de valores nominales de los datos, para aquellos atributos que
        no hayan definido la lista en la declaración */
        for (i = 0; i < data.length; i++) {
            type = attribute[i].getType();

            if (type == NOMINAL && attribute[i].getNumNominalValues() == 0) {
                for (j = 0; j < data[0].size(); j++) {
                    element = (String) data[i].elementAt(j);

                    if (!(attribute[i].isNominalValue(element)) && !element.equals("?")) {
                        attribute[i].addNominalValue(element);
                    }
                }
            }
        }


        /* Leemos el rango de los datos, para aquellos atributos que no lo hayan definido
        en la lista en la declaración */
        for (i = 0; i < data[0].size(); i++) {
            for (j = 0; j < numAttributes; j++) {
                type = attribute[j].getType();

                if (type == INTEGER) {
                    element = (String) data[j].elementAt(i);

                    if (!element.equals("?")) {
                        actualValueInt = Integer.valueOf(element);

                        if ((attribute[j].getFixedBounds()) == false) {
                            attribute[j].setBounds(actualValueInt, actualValueInt);
                        } else {
                            min = attribute[j].getMinAttribute();
                            max = attribute[j].getMaxAttribute();
                            if (actualValueInt < min) {
                                attribute[j].setBounds(actualValueInt, max);
                            }
                            if (actualValueInt > max) {
                                attribute[j].setBounds(min, actualValueInt);
                            }
                        }
                    }
                }


                if (type == REAL) {
                    element = (String) data[j].elementAt(i);

                    if (!element.equals("?")) {
                        actualValue = Double.valueOf(element);

                        if ((attribute[j].getFixedBounds()) == false) {
                            attribute[j].setBounds(actualValue, actualValue);
                        } else {
                            min = attribute[j].getMinAttribute();
                            max = attribute[j].getMaxAttribute();
                            if (actualValue < min) {
                                attribute[j].setBounds(actualValue, max);
                            }
                            if (actualValue > max) {
                                attribute[j].setBounds(min, actualValue);
                            }
                        }
                    }
                }
            }// end for

        }//end for




        f = new File(filename);
        f.delete();

        f = new File("temp");
        f.delete();

        super.Save(pathnameOutput);


    }//end start()
}//end WekaToKeel()

