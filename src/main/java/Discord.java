import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Discord extends ListenerAdapter {

    private  int listenType;

    private int maxItems;
    private  int itemsCount;

    private int voteCount;
    private int maxVote;

    private String[] items;
    private int[] votes;

    public static void main(String[] args) {
        JDA bot = JDABuilder.createDefault("MTAzNDE4MzU1OTg1MTgxMDg0Nw.GJoxaB.2zON672L5Rnl1WOofejL-P3gV1thvUenuHnkp8")
                .setActivity(Activity.playing("with rays sister"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Discord())
                .build();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getAuthor().isBot()) {

            if(itemsCount >= maxItems && voteCount < maxVote){
                if(Integer.parseInt(event.getMessage().getContentRaw()) <= maxItems) {
                    event.getChannel().sendMessage("vote has been recorded for " + event.getMessage().getContentRaw()).queue();
                    votes[Integer.parseInt(event.getMessage().getContentRaw()) - 1]++;
                    voteCount++;
                }
                if(voteCount == maxVote){
                    int max = 0;
                    int flag = 0;
                    for (int i = 0; i < votes.length; i++) {
                        max = votes[i] > votes[max] ? i : max;
                    }
                    for(int i = 0 ; i < votes.length; i++){
                        if(votes[max] == votes[i] && i != max){
                            if(flag == 0){
                                event.getChannel().sendMessage("theres a tie between...").queue();
                                event.getChannel().sendMessage(items[max]).queue();
                            }

                            event.getChannel().sendMessage(items[i]).queue();
                            flag = 1;
                        }
                    }
                    if(flag == 0)
                        event.getChannel().sendMessage("And the winner is...\n" + items[max]).queue();
                    maxItems = 0;
                    itemsCount = 0;

                    voteCount = 0;
                    maxVote = 0;


                    listenType = 0;
                }

            }

            if(listenType == 1 && itemsCount < maxItems){
                event.getChannel().sendMessage("item has been successfully entered.").queue();
                items[itemsCount] = event.getMessage().getContentRaw();
                itemsCount++;
                for(int i = 0; i < itemsCount; i++){
                    event.getChannel().sendMessage((i + 1) + ": " + items[i]).queue();
                }
            }
            if(listenType == 0) {
                if (event.getMessage().getContentRaw().matches("!polls \\d+ \\d+\\s*")) {
                    String[] arr = event.getMessage().getContentRaw().split(" ");
                    event.getChannel().sendMessage("The poll will now start until " + arr[1] + " people answer \nPlease enter " + arr[2] + " poll items...").queue();
                    maxItems = Integer.parseInt(arr[2]);
                    maxVote = Integer.parseInt(arr[1]);
                    items = new String[maxItems];
                    votes = new int[maxVote];
                    listenType = 1;
                }
            }

            if(event.getMessage().getContentRaw().matches("!help\\s*")){
                event.getChannel().sendMessage("please input \"!polls\" followed by the number of participants and number of seconds. Finally add the number of items inside the poll.\n After that input you may proceed to input the names of the items. After the items have been picked you may vote.").queue();
            }
        }
    }
}
