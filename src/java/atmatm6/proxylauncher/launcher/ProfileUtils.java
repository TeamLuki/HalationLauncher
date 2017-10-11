package atmatm6.proxylauncher.launcher;

import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.org.apache.xerces.internal.dom.DOMOutputImpl;
import com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl;
import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.io.*;
import java.net.URISyntaxException;

public class ProfileUtils {
    //TODO: Setup special directory movement thingy
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
            if (OS.contains("MAC")){
                try {
                    workingdirURL = ProfileUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                    workingdir = new File(workingdirURL);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                System.out.println(workingdir);
                if (workingdir.isDirectory() && workingdir.exists()){
                    if (workingdir.length() == 1){

                    }
                }
            }
            workingdirURL = System.getProperty("user.home") + "/ShanaLauncher";
            profileURL = workingdirURL + "/profiles.xml";
            settingsURL = workingdirURL + "/settings.xml";
        }
        if (!OS.contains("MAC")){
            workingdir = new File(workingdirURL);
        }
        profileFile = new File(profileURL);
        settingsFile = new File(settingsURL);
        workingdir.mkdirs();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            doc = docBuilder.parse(profileFile);
            ProfileUtils.write("p","");
        } catch (SAXException e) {
            try {
                profileFile.delete();
                InputStream in = getClass().getResourceAsStream("/shanaprofile.xml");
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                OutputStream out = new FileOutputStream(profileFile);
                out.write(buffer);
                out.close();
                doc = docBuilder.parse(profileFile);
            }catch (Exception ignored){}
        } catch (IOException | ParserConfigurationException e) {
            e.printStackTrace();
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
    static void write(String whatToWrite, Object o) throws IOException, SAXException, ParserConfigurationException {
        Text curacc;
        Element selacc;
        NodeList nl2;
        NodeList nl;
        Node accounts;
        JSONObject obj;
        switch (whatToWrite){
            case "remAuth":
                selacc = (Element) doc.getElementsByTagName("selectedAccount").item(0);
                accounts = doc.getElementsByTagName("accounts").item(0);
                nl = accounts.getChildNodes();
                for (int temp = 0; temp < nl.getLength(); temp++){
                    Node nNode = nl.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element nElement = (Element) nNode;
                        if (nElement.getAttributeNode("uuid").getValue().equals(selacc.getTextContent())) accounts.removeChild(nNode);
                    }
                }
                break;
            case "auth":
                obj = (JSONObject) o;
                JSONObject selprof = (JSONObject) obj.get("selectedProfile");
                boolean exists = false;
                accounts = doc.getElementsByTagName("accounts").item(0);
                nl = accounts.getChildNodes();
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
            case "firstProf":
                NodeList profiles = prof.getElementsByTagName("profile");
                for (int re = 0; re < profiles.getLength(); re+=1){
                    Node nNode = profiles.item(re);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element nElement = (Element) nNode;
                        if (nElement.hasAttribute("selected")) {
                            nElement.setAttribute("version",VersionUtils.getLatestVersion());
                            nElement.setAttribute("location",workingdirURL);
                        }
                    }
                }
                saveProfile();
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
            DOMSerializerImpl dsi = new DOMSerializerImpl();
            dsi.setNewLine("\n");
            dsi.getDomConfig().setParameter("format-pretty-print",true);
            DOMOutputImpl doi = new DOMOutputImpl();
            doi.setCharacterStream(new FileWriter(profileFile));
            dsi.write(prof, doi);
            doi.getCharacterStream().close();
        } catch (IOException ignored) {}
    }
    public static Object read(String whatToRead) throws InvalidArgumentException {
        try {
            doc = docBuilder.parse(settingsFile);
            prof = docBuilder.parse(profileFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        switch (whatToRead) {
            case "tokens":
                Element selacc = (Element) doc.getElementsByTagName("selectedAccount").item(0);
                Node accounts = doc.getElementsByTagName("accounts").item(0);
                NodeList nl = accounts.getChildNodes();
                String[] tokens = new String[2];
                boolean tokensexist = false;
                for (int temp = 0; temp < nl.getLength(); temp++){
                    Node nNode = nl.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element nElement = (Element) nNode;
                        String uuid = nElement.getAttributeNode("uuid").getTextContent();
                        if(uuid.equals(selacc.getTextContent())) {
                            tokens[0] = nElement.getElementsByTagName("acto").item(0).getTextContent();
                            tokens[1] = nElement.getElementsByTagName("clto").item(0).getTextContent();
                            tokensexist = true;
                            break;
                        }
                    }
                }
                if (tokensexist) return tokens;
                //bad practice (i think) but it's better than throwing (insert italics here)another(end of italics) exception.
                else return new String[]{"it's cold outside"};
            case "hostPort":
                Element location = (Element) doc.getElementsByTagName("location").item(0);
                String host = location.getElementsByTagName("host").item(0).getTextContent();
                String port = location.getElementsByTagName("port").item(0).getTextContent();
                return new String[]{host,port};
            case "profiles":
                NodeList profiles = doc.getElementsByTagName("profile");

                for (int c = 0; c < profiles.getLength(); c += 1){
                    Node nNode =  profiles.item(c);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element nElement = (Element) nNode;
                        if (nElement.hasAttribute("selected"));
                    }
                }
                return null;
            default:
                throw new IllegalArgumentException();
        }
    }
}