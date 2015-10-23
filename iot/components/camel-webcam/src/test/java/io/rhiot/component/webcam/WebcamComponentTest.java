/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rhiot.component.webcam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.dummy.WebcamDummyDevice;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import static org.junit.Assume.assumeTrue;

public class WebcamComponentTest extends CamelTestSupport {

    private static Webcam webcam = mock(Webcam.class);

    @BeforeClass
    public static void before() throws IOException {
        BufferedImage image = ImageIO.read(WebcamComponentTest.class.getResourceAsStream("rhiot.png"));
        given(webcam.getImage()).willReturn(image);
        given(webcam.open()).willReturn(true);
        given(webcam.getDevice()).willReturn(new WebcamDummyDevice(1));
        
    }
    
    @Test 
    public void testWebcamNames() throws Exception {
        
        //If we can find a webcam, we must have webcam names too
        assumeTrue(Webcam.getDefault() != null);
        
        WebcamComponent component = new WebcamComponent(context);
        component.doStart();
        
        assertFalse(component.getWebcamNames().isEmpty());
        assertEquals(Webcam.getDefault(), component.getWebcam(Webcam.getDefault().getName(), null));
        component.stop();
    }
    
    @Test 
    public void testWebcamFindByName() throws Exception {
        
        //If we can find a webcam, we must have webcam names too
        assumeTrue(Webcam.getDefault() != null);
        
        WebcamComponent component = new WebcamComponent(context);
        component.doStart();
        
        assertEquals(Webcam.getDefault(), component.getWebcam(Webcam.getDefault().getName(), null));
        component.stop();
    }
    
    @Test(expected = ClassNotFoundException.class)
    public void testDriverInstance() throws Exception {
        
        WebcamComponent component = new WebcamComponent(context);
        component.setDriver("invalid.driver");
        component.start();
    }
    
    @Test 
    public void testDriver() throws Exception {
        
        WebcamComponent component = new WebcamComponent(context);
        component.setDriver(CustomDriver.class.getName());
        component.start();
        
        assumeTrue(component.isStarted());
        component.stop();
    }
    
    @Test 
    public void testCompositeDriver() throws Exception {
        
        WebcamComponent component = new WebcamComponent(context);
        component.setDriver(CustomCompositeDriver.class.getName());
        component.start();
        
        assumeTrue(component.isStarted());
        component.stop();
    }

    @Test
    public void smokeTest() throws Exception {
        Thread.sleep(2000);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("webcam", webcam);
        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("webcam:cam?webcam=#webcam").to("seda:mock");
            }
        };
    }

}
