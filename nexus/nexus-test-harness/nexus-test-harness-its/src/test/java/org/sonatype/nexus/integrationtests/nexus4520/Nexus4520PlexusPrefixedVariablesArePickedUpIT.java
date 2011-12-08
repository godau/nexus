/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.integrationtests.nexus4520;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * IT for issue NEXUS-4520, covering the "System properties" case. In short, before Nexus is booted, it sets a
 * system propery like {@code plexus.foo=bar} (simulating option -Dplexus.foo=bar in wrapper.conf for example) and
 * then checks for the presence of {@code foo=bar} key-value pair in AppContext.
 *
 * @author: cstamas
 */
public class Nexus4520PlexusPrefixedVariablesArePickedUpIT
    extends AbstractNexusIntegrationTest
{

    public static final String SYSPROP_PREFIX = "plexus.";

    public static final String CUSTOM_SYSPROP_KEY = "customKey";

    public static final String CUSTOM_SYSPROP_VALUE = "customValue";

    @BeforeClass( alwaysRun = true )
    public void setSystemProperty()
        throws Exception
    {
        System.setProperty( SYSPROP_PREFIX + CUSTOM_SYSPROP_KEY, CUSTOM_SYSPROP_VALUE );
    }

    @AfterClass( alwaysRun = true )
    public void removeSystemProperty()
        throws Exception
    {
        System.getProperties().remove( SYSPROP_PREFIX + CUSTOM_SYSPROP_KEY );
    }

    private PrintStream actualOut;

    private ByteArrayOutputStream fakeOut;

    @BeforeClass( alwaysRun = true )
    public void grabOut()
        throws Exception
    {
        actualOut = System.out;

        System.setOut( new PrintStream( fakeOut = new ByteArrayOutputStream() ) );
    }

    @AfterClass( alwaysRun = true )
    public void restoreOut()
        throws Exception
    {
        if ( actualOut != null )
        {
            // if null mean @Before did not ran for some reason
            System.setOut( actualOut );
        }
    }

    @Test
    public void isCustomSyspropPresentInAppContext()
        throws IOException
    {
        final String bootLog = new String( fakeOut.toByteArray() );

        // the boot log (that is NOT in log, but in wrapper.log on real instances) should contain following line:
        // "customKey"="customValue" (raw: "customValue", src: prefixRemove(prefix:plexus., filter(keyStartsWith:[plexus.], system(properties))))
        // Note: I removed the complete line matching and am matching only against the presence of key-value: reason is that
        // this line (actually whole context "dump" where this line is) is generated by AppContext lib and not by Nexus,
        // and having this "full" match makes this test prone to fail if AppContext project changes it's output, something that
        // is not even direct relation to Nexus and would be hard to track down at first sight why this IT fails, while the
        // subject it tests _works_ (ie. would introduce false alarms too easily).
        assertThat( bootLog, containsString( String.format(
            "\"%s\"=\"%s\"", CUSTOM_SYSPROP_KEY, CUSTOM_SYSPROP_VALUE ) ) );
    }

}
