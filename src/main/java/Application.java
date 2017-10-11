import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by karis on 10/8/2017.
 *
 * This class handles the setting up of the connections to Twitter streams as well as closing them. It has the
 * capability of managing multiple connections via the 'clients' list but we shouldn't be abusing this as it
 * will cause slow down. One client connection should be fine for most purposes.
 */

public class Application {
    private Configs configs;
    private Hosts hosts;
    private StatusesFilterEndpoint filterEndpoint;
    private BlockingQueue<String> messageQueue;
    private BlockingQueue<Event> eventQueue;
    private Authentication auth;
    private List<Client> clients;
    private int clientNo;

    public static void main(String[] args) {
        final Application application = new Application();
        List<Integer> connections = new ArrayList<Integer>();
        connections.add(application.beginConnection());
        Scanner input = new Scanner(System.in);
        while(!input.hasNext()) { // While no key has been pressed
            // do code here
        }
        connections.forEach(connection -> application.closeConnection(connection));
    }

    public Application() {
        this.configs = new Configs();
        this.hosts = new HttpHosts(this.configs.getString("twitterHost"));
        this.filterEndpoint = new StatusesFilterEndpoint();
        this.messageQueue = new LinkedBlockingQueue<String>(10000);
        this.eventQueue = new LinkedBlockingQueue<Event>(1000);
        this.auth = new OAuth1(this.configs.getString("twitterConsumerKey"),
                this.configs.getString("twitterConsumerSecret"),
                this.configs.getString("twitterToken"),
                this.configs.getString("twitterSecret"));
        this.clientNo = 1;
        this.clients = new ArrayList<Client>();
    }

    public int beginConnection() {
        int connectionNo = this.clientNo;
        Client client = getClient();
        this.clients.add(client);
        client.connect();
        return connectionNo;
    }

    public void closeConnection(int which) {
        if(which < 0 || which >= this.clients.size()) return;
        Client client = this.clients.remove(which);
        client.stop();
    }

    private Client getClient() {
        String clientName = "hsbClient" + this.clientNo;
        ++this.clientNo;
        return new ClientBuilder()
                .name(clientName)
                .hosts(this.hosts)
                .authentication(this.auth)
                .endpoint(this.filterEndpoint)
                .processor(new StringDelimitedProcessor(this.messageQueue))
                .eventMessageQueue(this.eventQueue)
                .build();
    }
}
