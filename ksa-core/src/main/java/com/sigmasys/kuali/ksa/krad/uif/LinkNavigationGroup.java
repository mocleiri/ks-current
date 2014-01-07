package com.sigmasys.kuali.ksa.krad.uif;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.NavigationGroup;
import org.kuali.rice.krad.uif.element.Link;

import java.util.HashSet;
import java.util.Set;

/**
 * The custom navigation group. It allows to use text links in a navigation widget with KRAD.
 *
 * @author Michael Ivanov
 */
public class LinkNavigationGroup extends NavigationGroup {

    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> components = super.getSupportedComponents();
        if ( components == null ) {
            components = new HashSet<Class<? extends Component>>();
        }
        components.add(Link.class);
        return components;
    }

}
