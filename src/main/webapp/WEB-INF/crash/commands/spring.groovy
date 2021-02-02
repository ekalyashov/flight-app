package crash.commands

import org.crsh.cli.Option
import org.crsh.cli.Usage
import org.crsh.cli.Command
import org.crsh.cli.Argument

import org.crsh.cli.Required
import org.crsh.text.ui.Overflow
import org.crsh.text.ui.UIBuilder

@Usage("Spring commands")
class spring {

    @Usage("list the beans")
    @Command
    public void ls() {
        def ui = new UIBuilder().table(overflow: Overflow.HIDDEN, rightCellPadding: 1) {
            row(decoration: bold, foreground: black, background: white) {
                label("BEAN"); label("TYPE"); label("VALUE")
            }
            context.attributes.beans.each { key, value ->
                row() {
                    label(key, foreground: red); label(value?.getClass()?.simpleName); label("" + value)
                }
            }
            row(decoration: bold, foreground: black, background: white) {
                label("----------------------------------")
            }
        }
        context.writer.print(ui);
    }

    @Usage("determines if the specified bean is a singleton or not")
    @Command
    public String singleton(@Usage("the bean name") @Argument(name = 'bean name') @Required String name) {
        return "Bean $name is ${context.attributes.factory.isSingleton(name) ? '' : 'not '}a singleton";
    }

    @Usage("hibernate statistics for local database")
    @Command
    public String localStat(@Usage("type") @Option(names=["t","type"])  String type,
                             @Usage("sign") @Option(names=["s","sign"])  int sign,
                             @Usage("level") @Option(names=["l","level"])  int level) {
        return String.valueOf(context.attributes.beans["lFlightService"].getQueryStatistics(type, sign, level));
    }

    @Usage("hibernate statistics for external database")
    @Command
    public String externalStat(@Usage("type") @Option(names=["t","type"])  String type,
                            @Usage("sign") @Option(names=["s","sign"])  int sign,
                            @Usage("level") @Option(names=["l","level"])  int level) {
        return String.valueOf(context.attributes.beans["flightService"].getQueryStatistics(type, sign, level));
    }

    @Usage("current application properties list")
    @Command
    public void appProperties() {
        def ui = new UIBuilder().table(overflow: Overflow.HIDDEN, rightCellPadding: 1) {
            row(decoration: bold, foreground: black, background: white) {
                label("KEY"); label("VALUE")
            }
            context.attributes.beans["appConfig"].each { key, value ->
                row() {
                    label(key, foreground: red); label(value)
                }
            }
            row(decoration: bold, foreground: black, background: white) {
                label("----------------------------------")
            }
        }
        context.writer.print(ui);
    }
}
