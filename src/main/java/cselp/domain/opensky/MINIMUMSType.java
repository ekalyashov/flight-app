//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.30 at 12:57:23 PM MSK 
//


package cselp.domain.opensky;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MINIMUMSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MINIMUMSType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LANDING_VIS_VERTICAL" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="LANDING_VIS_HORIZONTAL" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="TAKE_OFF_VIS_HORIZONTAL" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MINIMUMSType", propOrder = {
    "landingvisvertical",
    "landingvishorizontal",
    "takeoffvishorizontal"
})
public class MINIMUMSType {

    @XmlElement(name = "LANDING_VIS_VERTICAL")
    protected Double landingvisvertical;
    @XmlElement(name = "LANDING_VIS_HORIZONTAL")
    protected Double landingvishorizontal;
    @XmlElement(name = "TAKE_OFF_VIS_HORIZONTAL")
    protected Double takeoffvishorizontal;

    /**
     * Gets the value of the landingvisvertical property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLANDINGVISVERTICAL() {
        return landingvisvertical;
    }

    /**
     * Sets the value of the landingvisvertical property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLANDINGVISVERTICAL(Double value) {
        this.landingvisvertical = value;
    }

    /**
     * Gets the value of the landingvishorizontal property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLANDINGVISHORIZONTAL() {
        return landingvishorizontal;
    }

    /**
     * Sets the value of the landingvishorizontal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLANDINGVISHORIZONTAL(Double value) {
        this.landingvishorizontal = value;
    }

    /**
     * Gets the value of the takeoffvishorizontal property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTAKEOFFVISHORIZONTAL() {
        return takeoffvishorizontal;
    }

    /**
     * Sets the value of the takeoffvishorizontal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTAKEOFFVISHORIZONTAL(Double value) {
        this.takeoffvishorizontal = value;
    }

}
