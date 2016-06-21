package com.restapp.utils;

import com.restapp.pojo.Bird;
import com.restapp.pojo.Sighting;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class XmlReaderAndWriter {
    public static List<Bird> readBirdFile(final String birdFilePath)
            throws Exception {
        List<Bird> birds = new ArrayList<>();
        Set<String> birdNameSet = new HashSet<>();
        File fXmlFile = new File(birdFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        NodeList nList = doc.getElementsByTagName("bird");
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String name = eElement.getElementsByTagName("name").item(0)
                        .getTextContent().trim().toLowerCase();
                String color = eElement.getElementsByTagName("color").item(0)
                        .getTextContent();
                double weight = Double.parseDouble(eElement
                        .getElementsByTagName("weight").item(0)
                        .getTextContent());
                double height = Double.parseDouble(eElement
                        .getElementsByTagName("height").item(0)
                        .getTextContent());
                if (name.isEmpty())
                    throw new Exception("bird name cannot be empty");
                Bird bird = new Bird(name, color, weight, height);
                if (birdNameSet.contains(name)) {
                    throw new Exception("duplicate bird " + name);
                } else {
                    birds.add(bird);
                }
            }
        }

        return birds;
    }

    public static Map<String, List<Sighting>> readSightingFile(
            final String sightingFilePath) throws Exception {
        Map<String, List<Sighting>> sightingsMap = new HashMap<>();

        File fXmlFile = new File(sightingFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        NodeList nList = doc.getElementsByTagName("sighting");
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String birdName = eElement.getAttribute("birdname");
                String location = eElement.getElementsByTagName("location")
                        .item(0).getTextContent();
                Date date = Common.dateFormatWithTime.parse((eElement.getElementsByTagName(
                        "date").item(0).getTextContent()));
                if (sightingsMap.containsKey(birdName)) {
                    sightingsMap.get(birdName)
                            .add(new Sighting(location, date));
                } else {
                    List<Sighting> sightings = new ArrayList<>();
                    sightings.add(new Sighting(location, date));
                    sightingsMap.put(birdName, sightings);
                }

            }
        }
        return sightingsMap;
    }

    public static void writeData(final List<Bird> birds,
                                 final String birdFilePath, final String sightingsFilePath)
            throws Exception {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document docBird = docBuilder.newDocument();
            Element rootElementBird = docBird.createElement("birds");
            docBird.appendChild(rootElementBird);

            Document docSighting = docBuilder.newDocument();
            Element rootElementSighting = docSighting
                    .createElement("sightings");
            docSighting.appendChild(rootElementSighting);

            for (Bird bird : birds) {
                Element birdElement = docBird.createElement("bird");
                rootElementBird.appendChild(birdElement);

                Element nameElement = docBird.createElement("name");
                nameElement.appendChild(docBird.createTextNode(bird.getName()));
                birdElement.appendChild(nameElement);

                Element colorElement = docBird.createElement("color");
                colorElement
                        .appendChild(docBird.createTextNode(bird.getColor()));
                birdElement.appendChild(colorElement);

                Element heightElement = docBird.createElement("height");
                heightElement.appendChild(docBird.createTextNode(Double
                        .toString(bird.getHeight())));
                birdElement.appendChild(heightElement);

                Element weightElement = docBird.createElement("weight");
                weightElement.appendChild(docBird.createTextNode(Double
                        .toString(bird.getWeight())));
                birdElement.appendChild(weightElement);

                for (Sighting sighting : bird.getSightings()) {
                    Element sightingElement = docSighting
                            .createElement("sighting");
                    rootElementSighting.appendChild(sightingElement);
                    sightingElement.setAttribute("birdname", bird.getName());
                    Element locationElement = docSighting
                            .createElement("location");
                    locationElement.appendChild(docSighting
                            .createTextNode(sighting.getLocation()));
                    sightingElement.appendChild(locationElement);

                    Element dateElement = docSighting.createElement("date");
                    dateElement.appendChild(docSighting
                            .createTextNode(Common.dateFormatWithTime.format(sighting
                                    .getDate())));
                    sightingElement.appendChild(dateElement);

                }

            }
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource birdDOM = new DOMSource(docBird);
            StreamResult birdFile = new StreamResult(new File(birdFilePath));
            transformer.transform(birdDOM, birdFile);

            DOMSource sightingDOM = new DOMSource(docSighting);
            StreamResult sightingFile = new StreamResult(new File(
                    sightingsFilePath));
            transformer.transform(sightingDOM, sightingFile);

        } catch (Exception ex) {
            throw new Exception("error writing to files " + birdFilePath
                    + " , " + sightingsFilePath, ex);

        }
    }

}
