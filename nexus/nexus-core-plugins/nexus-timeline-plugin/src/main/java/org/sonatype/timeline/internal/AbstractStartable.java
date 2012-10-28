/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.timeline.internal;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.timeline.TimelineConfiguration;

public abstract class AbstractStartable
{
    private final Logger logger;

    private volatile boolean started;

    private TimelineConfiguration configuration;

    protected AbstractStartable()
    {
        this.logger = LoggerFactory.getLogger( getClass() );
        this.started = false;
    }

    public final synchronized void start( TimelineConfiguration config )
        throws IOException
    {
        this.configuration = config;
        doStart();
        this.started = true;
    }

    public final synchronized void stop()
        throws IOException
    {
        if ( started )
        {
            this.started = false;
            doStop();
        }
    }

    public final synchronized boolean isStarted()
    {
        return started;
    }

    // ==

    protected Logger getLogger()
    {
        return logger;
    }

    protected TimelineConfiguration getConfiguration()
    {
        return configuration;
    }

    // ==

    protected abstract void doStart()
        throws IOException;

    protected abstract void doStop()
        throws IOException;
}