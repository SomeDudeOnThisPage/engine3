package engine3.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Arguments {
  @Parameter(names = {"--platform", "-p"}, description = "root folder of the engine platform resources")
  public String platform = "platform";

  @Parameter(names = {"--game", "-g"}, description = "root folder of the game resources")
  public String game = "platform/splash";

  public void parse(String[] args) {
    JCommander.newBuilder()
        .addObject(this)
        .build()
        .parse(args);
  }
}
