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
package org.candlepin.auth;

import org.candlepin.auth.permissions.Permission;
import org.candlepin.util.Util;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * An entity interacting with Candlepin
 */
public abstract class Principal implements Serializable {

    private static final long serialVersionUID = 907789978604269132L;

    private static Logger log = LoggerFactory.getLogger(Principal.class);
    protected List<Permission> permissions = new ArrayList<Permission>();

    public abstract String getType();

    public abstract boolean hasFullAccess();

    protected void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public boolean canAccess(Object target, SubResource subResource, Access access) {
        log.debug("{} principal checking for {} access to target: {} sub-resource: {}",
            new Object [] {this.getClass().getName(), access, target, subResource});

        if (hasFullAccess()) {
            return true;
        }

        for (Permission permission : permissions) {
            log.debug(" checking permission: {}", permission.getClass().getName());
            if (permission.canAccess(target, subResource, access)) {
                log.debug("  permission granted");
                // if any of the principal's permissions allows access, then
                // we are good to go
                return true;
            }
        }

        // none of the permissions grants access, so this target is not allowed
        log.warn("Refused principal: '" + getPrincipalName() + "' access to: " +
            target.getClass().getName());
        return false;
    }

    public abstract String getPrincipalName();

    public PrincipalData getData() {
        return new PrincipalData(this.getType(), this.getPrincipalName());
    }

    /**
     * @return Username for this principal, null if there is not one.
     */
    public String getUsername() {
        return null;
    }

    @Override
    public String toString() {
        return Util.toJson(this.getData());
    }

    /**
     * Assembles all criteria from all permissions. We use an *or* for this, so a principal
     * could carry permissions for multiple owners (for example), but still have their
     * results filtered without one of the perms hiding the results from the other.
     *
     * @param entityClass Type of object we're querying.
     * @return All criterion from all permissions or'd together.
     */
    public Criterion getCriteriaRestrictions(Class entityClass) {
        Criterion finalCriterion = null;

        // Admins do not need query filtering enabled.
        if (hasFullAccess()) {
            return finalCriterion;
        }

        List<Criterion> filters = new LinkedList<Criterion>();
        for (Permission p : permissions) {
            Criterion crit = p.getCriteriaRestrictions(entityClass);
            if (crit != null) {
                if (finalCriterion == null) {
                    finalCriterion = crit;
                }
                else {
                    finalCriterion = Restrictions.or(finalCriterion, crit);
                }
                filters.add(crit);
            }
        }
        return finalCriterion;
    }

}
