package org.wisdom.engine.server;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.wisdom.api.Controller;
import org.wisdom.api.DefaultController;
import org.wisdom.api.configuration.ApplicationConfiguration;
import org.wisdom.api.content.ContentEngine;
import org.wisdom.api.content.ContentSerializer;
import org.wisdom.api.error.ErrorHandler;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Renderable;
import org.wisdom.api.http.Result;
import org.wisdom.api.router.Route;
import org.wisdom.api.router.RouteBuilder;
import org.wisdom.api.router.Router;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Check the wisdom server behavior.
 * This class is listening for http requests on the port 9001.
 */
public class WisdomServerTest {

    private WisdomServer server;

    @After
    public void tearDown() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Test
    public void testServerStartSequence() throws InterruptedException, IOException {
        // Prepare the configuration
        ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
        when(configuration.getIntegerWithDefault(eq("http.port"), anyInt())).thenReturn(9001);
        when(configuration.getIntegerWithDefault(eq("https.port"), anyInt())).thenReturn(-1);

        // Prepare an empty router.
        Router router = mock(Router.class);

        // Configure the server.
        server = new WisdomServer(new ServiceAccessor(
                null,
                configuration,
                router,
                null,
                null,
                Collections.<ErrorHandler>emptyList(),
                null
        ));

        server.start();
        URL url = new URL("http://localhost:9001/test");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        assertThat(connection.getResponseCode()).isEqualTo(404);

        assertThat(server.hostname()).isEqualTo("localhost");
        assertThat(server.httpPort()).isEqualTo(9001);
        assertThat(server.httpsPort()).isEqualTo(-1);
    }

    @Test
    public void testOk() throws InterruptedException, IOException {
        // Prepare the configuration
        ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
        when(configuration.getIntegerWithDefault(eq("http.port"), anyInt())).thenReturn(9001);

        // Prepare the router with a controller
        Controller controller = new DefaultController() {
            @SuppressWarnings("unused")
            public Result index() {
                return ok("Alright");
            }
        };
        Router router = mock(Router.class);
        Route route = new RouteBuilder().route(HttpMethod.GET)
                .on("/")
                .to(controller, "index");
        when(router.getRouteFor("GET", "/")).thenReturn(route);

        // Configure the server.
        server = new WisdomServer(new ServiceAccessor(
                null,
                configuration,
                router,
                null,
                null,
                Collections.<ErrorHandler>emptyList(),
                null
        ));

        server.start();
        URL url = new URL("http://localhost:9001/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        assertThat(connection.getResponseCode()).isEqualTo(200);
        String body = IOUtils.toString(connection.getInputStream());
        assertThat(body).isEqualTo("Alright");
    }

    @Test
    public void testInternalError() throws InterruptedException, IOException {
        // Prepare the configuration
        ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
        when(configuration.getIntegerWithDefault(eq("http.port"), anyInt())).thenReturn(9001);

        // Prepare the router with a controller
        Controller controller = new DefaultController() {
            @SuppressWarnings("unused")
            public Result index() throws IOException {
                throw new IOException("My bad");
            }
        };
        Router router = mock(Router.class);
        Route route = new RouteBuilder().route(HttpMethod.GET)
                .on("/")
                .to(controller, "index");
        when(router.getRouteFor("GET", "/")).thenReturn(route);

        // Configure the content engine.
        ContentSerializer serializer = new ContentSerializer() {
            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public void serialize(Renderable<?> renderable) {
                if (renderable.content() instanceof Exception) {
                    renderable.setSerializedForm(((Exception) renderable.content()).getMessage());
                }
            }
        };
        ContentEngine contentEngine = mock(ContentEngine.class);
        when(contentEngine.getContentSerializerForContentType(anyString())).thenReturn(serializer);

        // Configure the server.
        server = new WisdomServer(new ServiceAccessor(
                null,
                configuration,
                router,
                contentEngine,
                null,
                Collections.<ErrorHandler>emptyList(),
                null
        ));

        server.start();
        URL url = new URL("http://localhost:9001/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        assertThat(connection.getResponseCode()).isEqualTo(500);
    }
}
