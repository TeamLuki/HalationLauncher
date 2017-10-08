package atmatm6.proxylauncher.utils;

import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
    private static Document prof;
    private static String OS = (System.getProperty("os.name")).toUpperCase();

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
        DocumentBuilder docBuilder = null;
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
                prof = docBuilder.parse(profileFile);
            } catch (IOException | SAXException e) {
                e.printStackTrace();
            }
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
                doc = docBuilder.parse(settingsFile);
            } catch (IOException | SAXException e) {
                e.printStackTrace();
            }
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
                Node accounts = doc.getElementById("accounts");
                NodeList nl = accounts.getChildNodes();

                boolean exists = false;
                for (int temp = 0; temp < nl.getLength(); temp++){
                    Node nNode = nl.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE){
                        Element nElement = (Element) nNode;
                        if (nElement.getAttribute("clientToken") == obj.get("clientToken")) {exists = true; break;}
                    }
                }
                if (!exists) {
                    Element account = doc.createElement("account");
                    Attr clid = doc.createAttribute("clientToken");
                    clid.setValue((String) obj.get("clientToken"));
                    Element uuid = doc.createElement("uuid");
                    Text uuidt = doc.createTextNode((String) selprof.get("uuid"));
                    uuid.appendChild(uuidt);
                    Element name = doc.createElement("name");
                    Text namet = doc.createTextNode((String) selprof.get("name"));
                    name.appendChild(namet);
                    Element acto = doc.createElement("acto");
                    Text actor = doc.createTextNode((String) obj.get("accessToken"));
                    acto.appendChild(actor);
                    account.appendChild(uuid);
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
                break;
            case "newProf":
                throw new NotImplementedException();
            default:
                throw new IllegalArgumentException("Not a valid argument lol");
        }
    }
}