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
package org.candlepin.auth.permissions;

import org.candlepin.auth.Access;
import org.candlepin.auth.SubResource;
import org.candlepin.model.Consumer;
import org.candlepin.model.Owner;
import org.candlepin.model.User;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * A permission allowing a user access to consumers in their org only if they were the ones
 * to register them, as determined by the username on the consumer.
 */
public class UsersConsumersPermission implements Permission {

    private User user;
    private Owner owner;
    private Access access;

    public UsersConsumersPermission(User u, Owner o, Access a) {
        this.user = u;
        this.owner = o;
        this.access = a;
    }


    @Override
    public boolean canAccess(Object target, SubResource subResource, Access action) {
        if (target.getClass().equals(Consumer.class)) {
            return ((Consumer) target).getOwner().getKey().equals(owner.getKey()) &&
                ((Consumer) target).getUsername().equals(user.getUsername()) &&
                providesAccess(action);
        }

        if (target.getClass().equals(Owner.class) &&
            subResource.equals(SubResource.CONSUMERS) &&
            (action.equals(Access.CREATE) || action.equals(Access.READ_ONLY))) {
            return true;
        }


        return false;
    }

    /**
     * Return true if this permission provides the requested access type.
     * If we have ALL, assume a match, otherwise do an explicit comparison.
     *
     * @return true if we provide the given access level.
     */
    public boolean providesAccess(Access requiredAccess) {
        // TODO: more this and all it's copies onto Access:
        return (this.access == Access.ALL || this.access == requiredAccess ||
            (this.access == Access.CREATE && requiredAccess == Access.READ_ONLY));
    }

    @Override
    public Criterion getCriteriaRestrictions(Class entityClass) {
        if (entityClass.equals(Consumer.class)) {
            return Restrictions.eq("username", user.getUsername());
        }
        return null;
    }

    @Override
    public Owner getOwner() {
        return owner;
    }
}
