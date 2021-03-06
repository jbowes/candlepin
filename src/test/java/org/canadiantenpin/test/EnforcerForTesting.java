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
package org.canadianTenPin.test;

import java.util.List;

import org.canadianTenPin.model.Consumer;
import org.canadianTenPin.model.Entitlement;
import org.canadianTenPin.model.Pool;
import org.canadianTenPin.policy.ValidationResult;
import org.canadianTenPin.policy.js.entitlement.Enforcer;
import org.canadianTenPin.policy.js.entitlement.PreUnbindHelper;
import org.canadianTenPin.policy.js.pool.PoolHelper;


/**
 * EnforcerForTesting
 */
public class EnforcerForTesting implements Enforcer {

    @Override
    public PoolHelper postEntitlement(
            Consumer consumer, PoolHelper postEntHelper, Entitlement ent) {
        return postEntHelper;
    }

    @Override
    public ValidationResult preEntitlement(Consumer consumer, Pool enitlementPool,
            Integer quantity) {
        return new ValidationResult();
    }

    @Override
    public ValidationResult preEntitlement(Consumer consumer, Pool enitlementPool,
            Integer quantity, CallerType caller) {
        return new ValidationResult();
    }

    public PreUnbindHelper preUnbind(Consumer consumer, Pool entitlementPool) {
        return new PreUnbindHelper(null);
    }

    public PoolHelper postUnbind(Consumer consumer, PoolHelper postEntHelper,
            Entitlement ent) {
        return postEntHelper;
    }

    @Override
    public List<Pool> filterPools(Consumer consumer, List<Pool> pools,
        boolean showAll) {
        return pools;
    }
}
