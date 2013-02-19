/*
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
package org.sonatype.nexus.plugins.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Object;
import java.lang.String;
import java.util.Properties;

import org.sonatype.nexus.logging.AbstractLoggingComponent;

/**
 *
 */
public abstract class UiSnippetBuilder<T>
    extends AbstractLoggingComponent
{

    protected final Object owner;

    protected final String groupId;

    protected final String artifactId;

    protected String encoding = "UTF-8";

    public UiSnippetBuilder( final Object owner, final String groupId, final String artifactId )
    {
        this.owner = checkNotNull( owner );
        this.groupId = checkNotNull( groupId );
        this.artifactId = checkNotNull( artifactId );
    }

    /**
     * Attempt to detect version from the POM of owner.
     */
    protected String detectVersion() {
        Properties props = new Properties();

        String path = String.format("/META-INF/maven/%s/%s/pom.properties", groupId, artifactId);
        InputStream input = owner.getClass().getResourceAsStream(path);

        if (input == null) {
            getLogger().warn("Unable to detect version; failed to load: {}", path);
            return null;
        }

        try {
            props.load(input);
        }
        catch (IOException e) {
            getLogger().warn("Failed to load POM: {}", path, e);
            return null;
        }

        return props.getProperty("version");
    }

    /**
     * Return a string suitable for use as suffix to a plain-URL to enforce version/caching semantics.
     */
    protected String getCacheBuster() {
        String version = detectVersion();
        if (version == null) {
            return "";
        }
        else if (version.endsWith("SNAPSHOT")) {
            // append timestamp for SNAPSHOT versions to help sort out cache problems
            return String.format("?v=%s&t=%s", version, System.currentTimeMillis());
        }
        else {
            return "?v=" + version;
        }
    }

    /**
     * Returns the default relative url for the given extension.
     *
     * @param extension The file extension.
     * @return A relative url of the form "static/$extension/$artifactId-all.$extension".
     */
    protected String getDefaultPath( final String extension )
    {
        return String.format("static/%s/%s-all.%s", extension, artifactId, extension );
    }

    public abstract T build();
}
