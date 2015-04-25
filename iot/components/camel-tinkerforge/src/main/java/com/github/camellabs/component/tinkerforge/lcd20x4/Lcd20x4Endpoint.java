/**
 * Licensed to the Camel Labs under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.component.tinkerforge.lcd20x4;

import com.github.camellabs.component.tinkerforge.TinkerforgeComponent;
import com.github.camellabs.component.tinkerforge.TinkerforgeEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Lcd20x4Endpoint extends TinkerforgeEndpoint {
    @UriParam private String uid = "Lcd1";
    @UriParam private String host = "localhost";
    @UriParam private int port = 4223;
    @UriParam private short line = 0;
    @UriParam private short position = 0;
    @UriParam private boolean cursor = false;
    @UriParam private boolean blink = false;
	
	private Lcd20x4Producer producer;
    
    public Lcd20x4Endpoint(String uri, TinkerforgeComponent tinkerforgeComponent) throws IOException {
		super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
        return producer != null ? producer : (producer = new Lcd20x4Producer(this));
    }

    @Override
	public Consumer createConsumer(Processor processor) throws Exception {
		throw new Exception("Cannot create a consumer object since the brickletType 'lcd20x4' cannot generate events.");
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public boolean getBlink() {
        return blink;
    }

    public boolean getCursor() {
        return cursor;
    }

    public short getLine() {
        return line;
    }

    public short getPosition() {
        return position;
    }

    public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setLine(short line) {
		this.line = line;
	}

	public void setPosition(short position) {
		this.position = position;
	}

	public void setCursor(boolean cursor) {
		this.cursor = cursor;
	}

	public void setBlink(boolean blink) {
		this.blink = blink;
	}

	public boolean isSingleton() {
        return false;
    }
}
