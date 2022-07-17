package engine3.asset.manifest;

import engine3.asset.AssetDefinitionFile;
import engine3.asset.AssetReference;
import engine3.asset.api.IAsset;
import engine3.asset.api.IAssetReference;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class AssetManifest {
  private final HashMap<String, IAssetReference<? extends IAsset>> assets;
  private final List<String> files;
  private final String name;

  private final ZipFile zip;

  private File manifest;
  private final boolean compressed;

  public final boolean isCompressed() {
    return this.compressed;
  }

  public final int getAssetCount() {
    return this.assets.size();
  }

  public final IAssetReference<? extends IAsset> getAssetReference(final String key) {
    return this.assets.get(key);
  }

  public final Collection<IAssetReference<? extends IAsset>> getAssetReferences() {
    return this.assets.values();
  }

  public final void write(final String path) {
    final String manifest = AssetDefinitionFile.write(this.assets.values());
    System.out.println(manifest);

    final ZipFile output = (this.zip == null) ? new ZipFile(this.name) : this.zip;
    final List<String> files = new LinkedList<>();
    for (final IAssetReference<? extends IAsset> reference : this.assets.values()) {
      reference.getMetaData();
    }
  }

  public AssetManifest(final String file) {
    this.name = file;
    this.assets = new HashMap<>();
    this.files = new LinkedList<>();

    this.zip = new ZipFile(file);
    this.compressed = zip.isValidZipFile();

    List<AssetReference<IAsset>> references = null;

    try {
      if (this.compressed) {
        final FileHeader manifestHeader = zip.getFileHeader("manifest.xml");

        if (manifestHeader != null) {
          this.manifest = new File(manifestHeader.getFileName());
          ZipInputStream is = zip.getInputStream(manifestHeader);
          references = AssetDefinitionFile.load(is.readAllBytes());
        }
      } else {
        references = AssetDefinitionFile.load(Files.readAllBytes(Path.of(file + "/manifest.xml")));
      }

      if (references != null) {
        for (final AssetReference<IAsset> reference : references) {
          this.assets.put(reference.getKey(), reference);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
