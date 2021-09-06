package engine3.asset;

import engine3.Engine3;
import engine3.asset.api.IAsset;
import engine3.asset.api.IAssetFactory;
import engine3.asset.api.IAssetReference;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class AssetLoadThread extends Thread {

  private boolean terminated;

  @Override
  public void run() {
    while (!terminated) {

      // iterate all async queues of the asset manager
      if (Engine3.ASSET_MANAGER.queue.isEmpty()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        continue;
      }

      while (!Engine3.ASSET_MANAGER.queue.isEmpty()) {
        try {
          AssetLoadTask<? extends IAsset> task = Engine3.ASSET_MANAGER.queue.poll();

          Element element = (Element) task.getData();
          AssetReference<? extends IAsset> reference = task.getReference();
          IAssetFactory<? extends IAsset> factory = Engine3.ASSET_MANAGER.getAssetFactory(reference.getType());

          JAXBContext context = JAXBContext.newInstance(factory.getXMLDataClass());
          Unmarshaller um = context.createUnmarshaller();

          IAsset asset = factory.load(um.unmarshal(element));
          reference.set(asset);
          reference.setLoadingStage(IAssetReference.LoadingStage.LOADED_ASYNCHRONOUS);

          Engine3.ASSET_MANAGER.loadSynchronous(reference);
        } catch(JAXBException e) {
          e.printStackTrace();
        }
      }


      /*try {
        // iterate queue
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

        for (String string : AssetManager.instance.loading) {
          DocumentBuilder builder = f.newDocumentBuilder();

          File file = new File(string);

          byte[] bytes = Files.readAllBytes(file.toPath());
          StringBuilder xmlStringBuilder = new StringBuilder(new String(bytes));
          xmlStringBuilder.insert(0, "<?xml version=\"1.0\"?>\n");

          ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes(StandardCharsets.UTF_8));
          Document document = builder.parse(input);

          // get assets in document
          Element root = (Element) document.getElementsByTagName("assets").item(0);
          NodeList children = root.getChildNodes();
          for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
              continue;
            }

            Element element = (Element) child; // asset creation info

            String tag = element.getTagName();
            String id = element.getAttribute("id");
            if (id == null) {
              System.err.println("could not load asset - id not defined");
              continue;
            }

            // get factory handling tag
            IAssetFactory<? extends IAsset> factory = AssetManager.instance.getAssetFactory(tag);
            if (factory != null) {
              // unmarshal
              JAXBContext context = JAXBContext.newInstance(factory.getXMLDataClass());
              Unmarshaller um = context.createUnmarshaller();

              // we assume that we don't make a fucky wucky when declaring which factory handles which tag
              // meaning we can "safely" cast the object in each factory
              // teehee
              IAsset asset = factory.load(um.unmarshal(child));
              asset.setKey(id); // set asset key so that the asset knows its' own id

              // load asset to asset reference
              AssetReference<? extends IAsset> reference = Engine3.ASSET_MANAGER.getReference(asset.getClass(), id);
              reference.finishLoadingASync();
              AssetManager.instance.addLoadedAsset(reference);
            } else {
              System.err.println("could not load asset - no factory handling tag '" + tag + "' found");
            }
          }

          // remove from queue
          AssetManager.instance.finishLoadingAssetPack(string);

        }
      } catch (IOException | ParserConfigurationException | SAXException | JAXBException e) {
        e.printStackTrace();
      }*/
    }
  }

  public void terminate() {
    this.terminated = true;
  }
}
