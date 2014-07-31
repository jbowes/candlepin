/**
 * Copyright (c) 2009 - 2012 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package org.canadianTenPin.resource.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.canadianTenPin.model.User;
import org.canadianTenPin.model.UserCurator;
import org.canadianTenPin.resource.AdminResource;
import org.canadianTenPin.service.UserServiceAdapter;
import org.canadianTenPin.service.impl.DefaultUserServiceAdapter;

import org.junit.Before;
import org.junit.Test;


/**
 * AdminResourceTest
 */
public class AdminResourceTest {

    private UserServiceAdapter usa;
    private AdminResource ar;
    private UserCurator uc;

    @Before
    public void init() {
        usa = mock(DefaultUserServiceAdapter.class);
        uc = mock(UserCurator.class);
        ar = new AdminResource(usa, uc);
    }

    @Test
    public void initialize() {
        when(uc.getUserCount()).thenReturn(new Long(0));
        assertEquals("Initialized!", ar.initialize());
        verify(usa).createUser(any(User.class));
    }

    @Test
    public void initWithNonDefaultUserService() {
        ar = new AdminResource(mock(UserServiceAdapter.class), uc);
        assertEquals("Already initialized.", ar.initialize());
    }

    @Test
    public void alreadyInitialized() {
        when(uc.getUserCount()).thenReturn(new Long(1000));
        assertEquals("Already initialized.", ar.initialize());
    }
}