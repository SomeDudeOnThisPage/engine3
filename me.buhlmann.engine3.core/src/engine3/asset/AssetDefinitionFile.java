package engine3.asset;

import engine3.asset.api.IAsset;
import engine3.asset.api.IAssetFactory;
import engine3.asset.api.IAssetReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class AssetDefinitionFile {
  @SuppressWarnings("unchecked")
  public static <T extends IAsset> void load(String path, AssetManager manager) {
    try {
      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = f.newDocumentBuilder();
      File file = new File(path);
      byte[] bytes = Files.readAllBytes(file.toPath());
      StringBuilder xmlStringBuilder = new StringBuilder(new String(bytes));
      xmlStringBuilder.insert(0, "<?xml version=\"1.0\"?>\n");

      ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
      Document document = builder.parse(input);

      Element root = (Element) document.getElementsByTagName("assets").item(0);
      NodeList children = root.getChildNodes();

      // create asset references
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        if (child.getNodeType() != Node.ELEMENT_NODE) {
          continue;
        }

        Element element = (Element) child; // asset creation info
        String tag = element.getTagName();
        String id = element.getAttribute("id");
        String counted = element.getAttribute("refcount");
        String loadType = element.getAttribute("loadtype");

        if (id == null) {
          System.err.println("could not load asset - id not defined");
          continue;
        }

        // get factory handling tag
        IAssetFactory<T> factory = (IAssetFactory<T>) manager.getAssetFactory(tag);
        if (factory != null) {
          AssetReference<T> reference = factory.createReference(Boolean.parseBoolean(counted));
          reference.setKey(id);
          manager.addAssetReference(reference);

          System.out.println("LOAD TYPE " + loadType);

          if (loadType == null || loadType.isEmpty() || loadType.equals("async")) {
            // if we load async, add to async loading queue
            manager.queue.add(new AssetLoadTask<>(child, reference));
          } else if (loadType.equals("sync")) {
            // otherwise instantiate the asset using the factory on the spot
            JAXBContext context = JAXBContext.newInstance(factory.getXMLDataClass());
            Unmarshaller um = context.createUnmarshaller();

            IAsset asset = factory.load(um.unmarshal(element));
            reference.set(asset);
            reference.setLoadingStage(IAssetReference.LoadingStage.LOADED_ASYNCHRONOUS);
            manager.initializeAsset(reference);
          }

        }
      }

    } catch (ParserConfigurationException | IOException | SAXException | JAXBException e) {
      e.printStackTrace();
    }
  }
}
