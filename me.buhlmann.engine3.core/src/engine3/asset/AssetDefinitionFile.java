package engine3.asset;

import engine3.Engine4;
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
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AssetDefinitionFile {

  public static <T extends IAsset, M extends IAssetFactory.MetaData> String write(final Collection<IAssetReference<? extends IAsset>> references) {
    try {
      final DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      final DocumentBuilder builder;
      builder = f.newDocumentBuilder();
      final Document document = builder.newDocument();
      final Element root = document.createElement("assets");
      for (IAssetReference<? extends IAsset> reference : references) {
        IAssetFactory<T, M> factory = (IAssetFactory<T, M>) Engine4.getAssetManager().getAssetFactory(reference.getType());
        final JAXBContext context = JAXBContext.newInstance(factory.getXMLDataClass());
        final Marshaller marshaller = context.createMarshaller();
        final DOMResult result = new DOMResult(document);
        marshaller.marshal(reference.getMetaData(), result);
        final Node e = result.getNode().getFirstChild();
        root.appendChild(e);
      }

      document.appendChild(root);
      final TransformerFactory transformerFactory = TransformerFactory.newInstance();
      final Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      final DOMSource source = new DOMSource(document);
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      transformer.transform(source, result);
      return writer.toString();
    } catch (ParserConfigurationException | TransformerException | JAXBException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static <T extends IAsset, M extends IAssetFactory.MetaData> List<AssetReference<T>> load(byte[] data) {
    final List<AssetReference<T>> assets = new ArrayList<>();
    try {
      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = f.newDocumentBuilder();
      StringBuilder xmlStringBuilder = new StringBuilder(new String(data));
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

        final Element element = (Element) child; // asset creation info
        final String tag = element.getTagName();
        final String id = element.getAttribute("id");
        final String counted = element.getAttribute("refcount");

        if (id == null) {
          Engine4.getLogger().error("skipped asset definition due to missing id");
          continue;
        }

        IAssetFactory<T, M> factory = (IAssetFactory<T, M>) Engine4.getAssetManager().getAssetFactory(tag);
        if (factory != null) {
          JAXBContext context = JAXBContext.newInstance(factory.getXMLDataClass());
          Unmarshaller um = context.createUnmarshaller();

          AssetReference<T> reference = factory.createReference(Boolean.parseBoolean(counted));
          reference.setKey(id);
          reference.setMetaData((IAssetFactory.MetaData) um.unmarshal(element));
          assets.add(reference);
        }
      }
    } catch (ParserConfigurationException | IOException | SAXException | JAXBException e) {
      e.printStackTrace();
    }

    return assets;
  }

  public static <T extends IAsset, M extends IAssetFactory.MetaData> void load(IAssetReference<T> reference, AssetManager manager) {
    // get factory handling tag
    IAssetFactory<T, M> factory = manager.getAssetFactory(reference.getType());
    if (factory != null) {
      manager.addAssetReference(reference);
      final String loadType = reference.getMetaData().initialization;

      if (loadType == null || loadType.isEmpty() || loadType.equals("async")) {
        // if we load async, add to async loading queue
        manager.queue.add(new AssetLoadTask<>(reference.getMetaData(), reference));
        Engine4.getLogger().trace("loading asset '" + reference.getKey() + "' asynchronously");
      } else if (loadType.equals("sync")) {
        T asset = factory.loadAssetSynchronous(reference.getMetaData());
        reference.set(asset);
        reference.setLoadingStage(IAssetReference.LoadingStage.LOADED_ASYNCHRONOUS);
        manager.initializeAsset(reference);
        Engine4.getLogger().trace("loaded and initialized asset '" + reference.getKey() + "' synchronously");
      } else {
        throw new UnsupportedOperationException("cannot load asset with initialization type " + loadType);
      }
    }
  }
}
