package engine3.asset;

import engine3.Engine4;
import engine3.asset.api.IAsset;
import engine3.asset.api.IAssetFactory;
import engine3.asset.api.IAssetReference;

public class AssetLoadThread extends Thread {

  private boolean terminated;

  @Override
  public void run() {
    while (!terminated) {

      // iterate all async queues of the asset manager
      if (Engine4.getAssetManager().queue.isEmpty()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        continue;
      }

      while (!Engine4.getAssetManager().queue.isEmpty()) {
        AssetLoadTask<? extends IAsset> task = Engine4.getAssetManager().queue.poll();

        IAssetReference<? extends IAsset> reference = task.getReference();
        IAssetFactory<? extends IAsset, ? extends IAssetFactory.MetaData> factory = Engine4.getAssetManager().getAssetFactory(reference.getType());

        IAsset asset = factory.loadAssetSynchronous(task.getData());
        reference.set(asset);
        reference.setLoadingStage(IAssetReference.LoadingStage.LOADED_ASYNCHRONOUS);

        Engine4.getAssetManager().loadSynchronous(reference);
      }
    }
  }

  public void terminate() {
    this.terminated = true;
  }
}
