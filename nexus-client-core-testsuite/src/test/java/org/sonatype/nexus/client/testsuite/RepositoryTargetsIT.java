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
package org.sonatype.nexus.client.testsuite;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collection;

import org.junit.Test;
import org.sonatype.nexus.client.core.subsystem.security.Privileges;
import org.sonatype.nexus.client.core.subsystem.security.Role;
import org.sonatype.nexus.client.core.subsystem.security.Roles;
import org.sonatype.nexus.client.core.subsystem.security.User;
import org.sonatype.nexus.client.core.subsystem.security.Users;
import org.sonatype.nexus.client.core.subsystem.targets.RepositoryTarget;
import org.sonatype.nexus.client.core.subsystem.targets.RepositoryTargets;

public class RepositoryTargetsIT
    extends NexusClientITSupport
{

    public RepositoryTargetsIT( final String nexusBundleCoordinates )
    {
        super( nexusBundleCoordinates );
    }

    @Test
    public void getTargets()
    {
        final Collection<RepositoryTarget> targets = targets().get();
        assertThat( targets, is( not( empty() ) ) );
    }

    /*
    @Test
    public void getRole()
    {
        final Role role = roles().get( "ui-search" );
        assertThat( role, is( notNullValue() ) );
        assertThat( role.id(), is( "ui-search" ) );
    }

    @Test
    public void createRole()
    {
        final String roleId = testName.getMethodName();
        roles().create( roleId )
            .withName( roleId )
            .withPrivilege( "19" )
            .save();
        final Role role = roles().get( roleId );
        assertThat( role, is( notNullValue() ) );
        assertThat( role.id(), is( roleId ) );
        assertThat( role.name(), is( roleId ) );
    }

    @Test
    public void updateRole()
    {
        final String roleId = testName.getMethodName();
        roles().create( roleId )
            .withName( roleId )
            .withPrivilege( "19" )
            .save()
            .withName( roleId + "Bar" )
            .save();
        final Role role = roles().get( roleId );
        assertThat( role, is( notNullValue() ) );
        assertThat( role.name(), is( roleId + "Bar" ) );
    }

    @Test
    public void deleteRole()
    {
        final String roleId = testName.getMethodName();
        final Role role = roles().create( roleId )
            .withName( roleId )
            .withPrivilege( "19" )
            .save();
        role.remove();
    }

    @Test
    public void refreshRole()
    {
        final String roleId = testName.getMethodName();
        Role role = roles().create( roleId )
            .withName( roleId )
            .withPrivilege( "19" )
            .save()
            .withName( roleId + "Bar" )
            .refresh();
        assertThat( role.id(), is( roleId ) );
        role = roles().get( roleId );
        assertThat( role, is( notNullValue() ) );
        assertThat( role.name(), is( roleId ) );
    }
    */

    private RepositoryTargets targets()
    {
        return client().getSubsystem( RepositoryTargets.class );
    }

}
