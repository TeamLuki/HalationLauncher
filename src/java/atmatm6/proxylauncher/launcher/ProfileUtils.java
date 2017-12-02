package atmatm6.proxylauncher.launcher;

import com.sun.javaws.exceptions.InvalidArgumentException;
import com.sun.org.apache.xerces.internal.dom.DOMOutputImpl;
import com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl;
import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;

public class ProfileUtils {
    //TODO: Setup special directory movement thingy
    private static String workingdirURL;
    private static String settingsURL;
    private static String defaultLocationURL;
    private static File workingdir;
    private static File settingsFile;
    private static File defaultLocation;
    private static Document doc;
    private static DocumentBuilder docBuilder = null;
    private static String OS = (System.getProperty("os.name")).toUpperCase();
    private static String sep;

    public void setup(){
        sep = File.separator;
        if (OS.contains("WIN")) workingdirURL = System.getenv("AppData") + sep+"ShanaLauncher";
        else if (OS.contains("MAC")){
            try {
                workingdirURL = ProfileUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else if (OS.contains("NIX")) {
            workingdirURL = System.getProperty("user.home") + "/ShanaLauncher";
        } else {
            JLabel label = new JLabel();
            Font font = label.getFont();
            StringBuilder style = new StringBuilder("font-family:" + font.getFamily() + ";");
            style.append("font-weight:")
                    .append(font.isBold() ? "bold" : "normal")
                    .append(";")
                    .append("font-size:")
                    .append(font.getSize())
                    .append("pt;");
            JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + style.toString() + "\">"+
                    "Minecraft isn't compatible with your system." +
                    "If you think this is wrong or Minecraft runs on your computer normally," +
                    "Please go and make an issue "
                    + "<a href=\"http://github.com/DreamLiveGitHub/HalationLauncher/issues\">here</a>" +
                    " under the compatibility label in said case."
                    + "</body></html>");
            ep.addHyperlinkListener(e -> {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
            });
            ep.setEditable(false);
            ep.setBackground(label.getBackground());

            JOptionPane.showMessageDialog(null, ep);
            System.exit(0);
        }
        defaultLocationURL = workingdirURL + sep+"minecraft";
        workingdir = new File(workingdirURL);
        defaultLocation = new File(defaultLocationURL);
        if (!workingdir.exists() | !workingdir.isDirectory() | defaultLocation.exists() | defaultLocation.isDirectory()) defaultLocation.mkdirs();
        settingsURL = workingdirURL + sep+"shanasettings.xml";
        settingsFile = new File(settingsURL);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            doc = docBuilder.parse(settingsFile);
        } catch (Exception e) {
            try {
                settingsFile.delete();
                settingsFile.createNewFile();
                InputStream in = getClass().getResourceAsStream("/shanasettings.xml");
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                OutputStream out = new FileOutputStream(settingsFile);
                out.write(buffer);
                out.close();
                doc = docBuilder.parse(settingsFile);
            }catch (Exception ignored){}
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
                if (!(doc.getElementsByTagName("clientToken").item(0) instanceof Element)){
                    Element e = doc.createElement("clientToken");
                    Text clietoke = doc.createTextNode((String) obj.get("clientToken"));
                    e.appendChild(clietoke);

                }
                if (!exists) {
                    Element account = doc.createElement("account");
                    Attr clid = doc.createAttribute("uuid");
                    clid.setValue((String) selprof.get("id"));
                    account.setAttributeNode(clid);
                    Element name = doc.createElement("name");
                    Text namet = doc.createTextNode((String) selprof.get("name"));
                    name.appendChild(namet);
                    Element acto = doc.createElement("acto");
                    Text actor = doc.createTextNode((String) obj.get("accessToken"));
                    acto.appendChild(actor);
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
    public static Object read(String whatToRead) throws InvalidArgumentException {
        try {
            doc = docBuilder.parse(settingsFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        switch (whatToRead) {
            case "tokens":
                Element selacc = (Element) doc.getElementsByTagName("selectedAccount").item(0);
                Node accounts = doc.getElementsByTagName("accounts").item(0);
                NodeList nl = accounts.getChildNodes();
                String[] tokens = new String[2];
                boolean accessTokenExists = false;
                for (int temp = 0; temp < nl.getLength(); temp++){
                    Node nNode = nl.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element nElement = (Element) nNode;
                        String uuid = nElement.getAttributeNode("uuid").getTextContent();
                        if(uuid.equals(selacc.getTextContent())) {
                            tokens[0] = nElement.getElementsByTagName("acto").item(0).getTextContent();
                            tokens[1] = doc.getElementsByTagName("clientToken").item(0).getTextContent();
                            accessTokenExists = true;
                            break;
                        }
                    }
                }
                if (accessTokenExists) return tokens;
                    //bad practice (i think) but it's better than throwing (insert italics here)another(end of italics) exception.
                else {
                    tokens[0] = "it's cold outside";
                    return tokens;
                }
            case "hostPort":
                Element location = (Element) doc.getElementsByTagName("location").item(0);
                String host = location.getElementsByTagName("host").item(0).getTextContent();
                String port = location.getElementsByTagName("port").item(0).getTextContent();
                return new String[]{host,port};
            case "workingDir":
                return workingdirURL;
            case "version":
                return doc.getElementsByTagName("version").item(0).getTextContent();
            default:
                throw new IllegalArgumentException();
        }
    }

    //assumes start of path (e.g. C:\Users or /home/) has been added
    public static String path(String... parts) throws Exception {
        if (parts.length <= 1) return "";
        StringBuilder path = new StringBuilder(parts[0]);
        for(int re=1; re<parts.length; re+=1){
            path.append(sep);
            path.append(parts[re]);
        }
        return path.toString();
    }
}