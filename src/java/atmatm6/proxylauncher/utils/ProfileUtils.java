package atmatm6.proxylauncher.utils;

import com.sun.org.apache.xerces.internal.dom.DOMOutputImpl;
import com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl;
import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class ProfileUtils {
    //TODO: Setup special directory moving tool
    private static String workingdirURL;
    private static String profileURL;
    private static String settingsURL;
    private static File workingdir;
    private static File profileFile;
    private static File settingsFile;
    private static Document doc;
    private static DocumentBuilder docBuilder = null;
    private static Document prof;
    private static String OS = (System.getProperty("os.name")).toUpperCase();
    private static Transformer transformer;

    public void setup(){
        if (OS.contains("WIN")) {
            workingdirURL = System.getenv("AppData") + "\\ShanaLauncher";
            profileURL = workingdirURL + "\\profiles.xml";
            settingsURL = workingdirURL + "\\settings.xml";
        } else {
            workingdirURL = System.getProperty("user.home") + "/ShanaLauncher";
            profileURL = workingdirURL + "/profiles.xml";
            settingsURL = workingdirURL + "/settings.xml";
        }
        workingdir = new File(workingdirURL);
        profileFile = new File(profileURL);
        settingsFile = new File(settingsURL);
        workingdir.mkdirs();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        if (!profileFile.exists()) {
            try {
                profileFile.createNewFile();
                InputStream in = getClass().getResourceAsStream("/shanaprofile.xml");
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                OutputStream out = new FileOutputStream(profileFile);
                out.write(buffer);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            prof = docBuilder.parse(profileFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        if (!settingsFile.exists()){
            try {
                settingsFile.createNewFile();
                InputStream in = getClass().getResourceAsStream("/shanasettings.xml");
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                OutputStream out = new FileOutputStream(settingsFile);
                out.write(buffer);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            doc = docBuilder.parse(settingsFile);
        } catch (SAXException e) {
            try {
                settingsFile.delete();
                InputStream in = getClass().getResourceAsStream("/shanasettings.xml");
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                OutputStream out = new FileOutputStream(settingsFile);
                out.write(buffer);
                out.close();
                doc = docBuilder.parse(settingsFile);
            }catch (Exception ignored){}
        } catch (IOException e) {
            e.printStackTrace();
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
    }

    static void write(String s, Object o) throws IOException, SAXException, ParserConfigurationException {
        Text curacc;
        Element selacc;
        NodeList nl2;
        JSONObject obj;
        switch (s){
            case "auth"://// TODO: Complete authentication setting writer
                obj = (JSONObject) o;
                JSONObject selprof = (JSONObject) obj.get("selectedProfile");
                boolean exists = false;
                Node accounts = doc.getElementsByTagName("accounts").item(0);
                NodeList nl = accounts.getChildNodes();
                for (int temp = 0; temp < nl.getLength(); temp++){
                    Node nNode = nl.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element nElement = (Element) nNode;
                        System.out.println(nElement.getAttributeNode("uuid").getValue().equals(selprof.get("id")));
                        if (nElement.getAttributeNode("uuid").getValue().equals(selprof.get("id"))) {exists = true; break;}
                    }
                }
                if (!exists) {
                    Element account = doc.createElement("account");
                    Attr clid = doc.createAttribute("uuid");
                    clid.setValue((String) selprof.get("id"));
                    account.setAttributeNode(clid);
                    Element clto = doc.createElement("clto");
                    Text cltot = doc.createTextNode((String) obj.get("clientToken"));
                    clto.appendChild(cltot);
                    Element name = doc.createElement("name");
                    Text namet = doc.createTextNode((String) selprof.get("name"));
                    name.appendChild(namet);
                    Element acto = doc.createElement("acto");
                    Text actor = doc.createTextNode((String) obj.get("accessToken"));
                    acto.appendChild(actor);
                    account.appendChild(clto);
                    account.appendChild(name);
                    account.appendChild(acto);
                    accounts.appendChild(account);
                }
                selacc = (Element) doc.getElementsByTagName("selectedAccount").item(0);
                nl2 = selacc.getChildNodes();
                for (int temp = 0; temp < nl2.getLength(); temp++){
                    selacc.removeChild(nl2.item(temp));
                }
                curacc = doc.createTextNode((String) obj.get("clientToken"));
                selacc.appendChild(curacc);
                saveDoc();
                break;
            case "setAcc":
                obj = (JSONObject) o;
                selacc = (Element) doc.getElementsByTagName("selectedAccount").item(0);
                nl2 = selacc.getChildNodes();
                for (int temp = 0; temp < nl2.getLength(); temp++){
                    selacc.removeChild(nl2.item(temp));
                }
                curacc = doc.createTextNode((String) obj.get("clientToken"));
                selacc.appendChild(curacc);
                saveDoc();
                break;
            case "newProf":
                throw new NotImplementedException();
            case "editProf":
                throw new NotImplementedException();
            default:
                throw new IllegalArgumentException("Not a valid argument lol");
        }
    }
    private static void saveDoc(){
        try {
            DOMSerializerImpl dsi = new DOMSerializerImpl();
            dsi.setNewLine("\n");
            dsi.getDomConfig().setParameter("format-pretty-print",true);
            DOMOutputImpl doi = new DOMOutputImpl();
            doi.setCharacterStream(new FileWriter(settingsFile));
            dsi.write(doc, doi);
            doi.getCharacterStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void saveProfile(){
        try {
            DOMSource source = new DOMSource(prof);
            StreamResult result = new StreamResult(profileFile);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    public static String[] read(String s){
        try {
            doc = docBuilder.parse(settingsFile);
            prof = docBuilder.parse(profileFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        switch (s) {
            case "accessToken":
                //ToDO: look for accessToken not use doc
                Element selacc = (Element) doc.getElementsByTagName("selectedAccount").item(0);
                Node accounts = doc.getElementsByTagName("accounts").item(0);
                NodeList nl = accounts.getChildNodes();
                for (int temp = 0; temp < nl.getLength(); temp++){
                    Node nNode = nl.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element nElement = (Element) nNode;
                        if (nElement.getElementsByTagName("acto").item(0).getTextContent().equals(selacc.getTextContent())) return new String[]{selacc.getTextContent()};
                    }
                }
                break;
            case "hostPort":
                try {
                    //ToDO: look for host and port not use doc
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(new StringWriter());
                    transformer.transform(source, result);
                    return new String[]{result.getWriter().toString()};
                } catch (TransformerException ignored){}
                break;
            case "profiles":
                break;
        }
        throw new NotImplementedException();
    }
}