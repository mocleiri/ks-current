package com.sigmasys.kuali.ksa.util;


import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.hibernate.UnEnhancer;
import net.sf.beanlib.hibernate3.Hibernate3BeanReplicator;
import net.sf.beanlib.provider.JavaBeanDetailedPropertyFilter;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import org.hibernate.Hibernate;

/**
 * This class contains useful methods for dealing with POJOs, JPA entities
 * and lazy-loaded JPA associations.
 *
 * @author Michael Ivanov
 */
public class BeanUtils {

    private static final HibernateBeanReplicator hibernateBeanReplicator;

    static {
        // Using Javassist instead of CGLIB (performance enhancement) at the global level
        UnEnhancer.setDefaultCheckCGLib(false);
        // Creating a hibernate bean replicator to copy Hibernate-managed entities
        hibernateBeanReplicator = new Hibernate3BeanReplicator();
        hibernateBeanReplicator.initDetailedPropertyFilter(JavaBeanDetailedPropertyFilter.ALWAYS_PROPAGATE);
        hibernateBeanReplicator.initCustomTransformerFactory(new LazyKillingBeanTransformerFactory());
    }

    /**
     * A custom bean transformer that sets non-initialized Hibernate entity properties to NULL
     */
    private static class LazyKillingBeanTransformerFactory implements CustomBeanTransformerSpi.Factory {
        @Override
        public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi beanTransformer) {
            return new CustomBeanTransformerSpi() {
                @Override
                public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
                    // apply custom transformation for the uninitialized properties
                    return !Hibernate.isInitialized(from);
                }

                @Override
                public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
                    // custom transform by not replicating the uninitialized properties
                    return null;
                }
            };
        }
    }

    private BeanUtils() {
    }

    private static void prepare() {
        // Using Javassist instead of CGLIB (performance enhancement) per thread
        UnEnhancer.setCheckCGLibForThisThread(false);
    }

    /**
     * Convenient method to deep copy the given object using the default behavior.
     *
     * @param entity an object that needs to be copied
     * @param <T>    an entity type
     * @return a deep copy of the given object
     */
    public static <T> T getDeepCopy(T entity) {
        prepare();
        // Making a deep copy with HibernateBeanReplicator
        return hibernateBeanReplicator.deepCopy(entity);
    }

    /**
     * Convenient method to shallow copy the given object using the default behavior.
     * Shallow copy means skipping those properties that are of type collection, map
     * or under a package that doesn't start with "java.".
     *
     * @param entity an object that needs to be copied
     * @param <T>    an entity type
     * @return a shallow copy of the given object
     */
    public static <T> T getShallowCopy(T entity) {
        prepare();
        // Making a shallow copy with HibernateBeanReplicator
        return hibernateBeanReplicator.shallowCopy(entity);
    }


}
