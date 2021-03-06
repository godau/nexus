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
package core.whitelist;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;
import org.sonatype.nexus.client.core.exception.NexusClientBadRequestException;
import org.sonatype.nexus.client.core.subsystem.repository.ProxyRepository;
import org.sonatype.nexus.client.core.subsystem.whitelist.DiscoveryConfiguration;
import org.sonatype.nexus.client.core.subsystem.whitelist.Status;
import org.sonatype.nexus.client.core.subsystem.whitelist.Status.Outcome;
import org.sonatype.sisu.litmus.testsupport.group.Smoke;

/**
 * Simple smoke IT for Whitelist REST being responsive and is reporting the expected statuses.
 * 
 * @author cstamas
 */
@Category( Smoke.class )
public class WhitelistSmokeIT
    extends WhitelistITSupport
{
    // we will timeout after 15 minutes, just as a safety net
    @Rule
    public Timeout timeout = new Timeout( 900000 );

    /**
     * Constructor.
     * 
     * @param nexusBundleCoordinates
     */
    public WhitelistSmokeIT( final String nexusBundleCoordinates )
    {
        super( nexusBundleCoordinates );
    }

    @Before
    public void waitForDiscoveryOutcome()
        throws Exception
    {
        waitForWLDiscoveryOutcome( "central" );
    }

    @Test
    public void checkPublicGroupResponse()
    {
        // public
        final Status publicStatus = whitelist().getWhitelistStatus( "public" );
        assertThat( publicStatus.getPublishedStatus(), equalTo( Outcome.SUCCEEDED ) );
    }

    @Test
    public void checkReleasesHostedResponse()
    {
        // releases
        final Status releasesStatus = whitelist().getWhitelistStatus( "releases" );
        assertThat( releasesStatus.getPublishedStatus(), equalTo( Outcome.SUCCEEDED ) );
        assertThat( releasesStatus.getDiscoveryStatus(), is( nullValue() ) );
    }

    @Test
    public void checkCentralProxyResponse()
    {
        // central
        final Status centralStatus = whitelist().getWhitelistStatus( "central" );
        assertThat( centralStatus.getPublishedStatus(), equalTo( Outcome.SUCCEEDED ) );
        assertThat( centralStatus.getDiscoveryStatus(), is( notNullValue() ) );
        assertThat( centralStatus.getDiscoveryStatus().getDiscoveryLastStatus(), equalTo( Outcome.SUCCEEDED ) );
    }

    @Test
    public void checkCentralProxyConfiguration()
    {
        // get configuration for central and check for sane values (actually, they should be defaults).
        {
            final DiscoveryConfiguration centralConfiguration = whitelist().getDiscoveryConfigurationFor( "central" );
            assertThat( centralConfiguration, is( notNullValue() ) );
            assertThat( centralConfiguration.isEnabled(), equalTo( true ) );
            assertThat( centralConfiguration.getIntervalHours(), equalTo( 24 ) );
            // checked ok, set interval to 12h
            centralConfiguration.setIntervalHours( 12 );
            whitelist().setDiscoveryConfigurationFor( "central", centralConfiguration );
        }
        // verify is set
        {
            final DiscoveryConfiguration centralConfiguration = whitelist().getDiscoveryConfigurationFor( "central" );
            assertThat( centralConfiguration, is( notNullValue() ) );
            assertThat( centralConfiguration.isEnabled(), equalTo( true ) );
            assertThat( centralConfiguration.getIntervalHours(), equalTo( 12 ) );
        }
    }

    @Test( expected = NexusClientBadRequestException.class )
    public void checkReleasesHostedHasNoDiscoveryConfiguration()
    {
        final DiscoveryConfiguration releasesConfiguration = whitelist().getDiscoveryConfigurationFor( "releases" );
    }

    @Test( expected = NexusClientBadRequestException.class )
    public void checkPublicGroupHasNoDiscoveryConfiguration()
    {
        final DiscoveryConfiguration releasesConfiguration = whitelist().getDiscoveryConfigurationFor( "public" );
    }

    @Test( expected = NexusClientBadRequestException.class )
    public void checkCentralM1ShadowHasNoDiscoveryConfiguration()
    {
        final DiscoveryConfiguration releasesConfiguration = whitelist().getDiscoveryConfigurationFor( "central-m1" );
    }

    @Test( expected = NexusClientBadRequestException.class )
    public void checkDiscoveryOnOutOfServiceRepository()
    {
        try
        {
            repositories().get( "central" ).putOutOfService().save();
            whitelist().updateWhitelist( "central" );
        }
        finally
        {
            repositories().get( "central" ).putInService().save();
        }
    }

    @Test( expected = NexusClientBadRequestException.class )
    public void checkDiscoveryOnBlockedProxyRepository()
    {
        try
        {
            repositories().get( ProxyRepository.class, "central" ).block().save();
            whitelist().updateWhitelist( "central" );
        }
        finally
        {
            repositories().get( ProxyRepository.class, "central" ).unblock().save();
        }
    }
}
