
package org.jboss.sepro.service.ws.skeleton;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.jboss.sepro.service.ws.skeleton package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Ping_QNAME = new QName("http://sepro.jboss.org", "ping");
    private final static QName _CallbackAsyncPing_QNAME = new QName("http://sepro.jboss.org", "callbackAsyncPing");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.sepro.service.ws.skeleton
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CallbackAsyncPing }
     * 
     */
    public CallbackAsyncPing createCallbackAsyncPing() {
        return new CallbackAsyncPing();
    }

    /**
     * Create an instance of {@link Ping }
     * 
     */
    public Ping createPing() {
        return new Ping();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sepro.jboss.org", name = "ping")
    public JAXBElement<Ping> createPing(Ping value) {
        return new JAXBElement<Ping>(_Ping_QNAME, Ping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CallbackAsyncPing }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sepro.jboss.org", name = "callbackAsyncPing")
    public JAXBElement<CallbackAsyncPing> createCallbackAsyncPing(CallbackAsyncPing value) {
        return new JAXBElement<CallbackAsyncPing>(_CallbackAsyncPing_QNAME, CallbackAsyncPing.class, null, value);
    }

}
