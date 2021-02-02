//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.30 at 12:57:23 PM MSK 
//


package cselp.domain.opensky;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FLIGHTType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FLIGHTType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CARRIER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FLIGHT_NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FLIGHT_DATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ACTUAL_DATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TAIL_NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ORIGIN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DESTINATION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FLT_KIND" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LEG" type="{}LEGType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FLIGHTType", propOrder = {
    "carrier",
    "flightno",
    "flightdate",
    "actualdate",
    "tailno",
    "origin",
    "destination",
    "fltkind",
    "leg"
})
public class FLIGHTType {

    @XmlElement(name = "CARRIER", required = true)
    protected String carrier;
    @XmlElement(name = "FLIGHT_NO", required = true)
    protected String flightno;
    @XmlElement(name = "FLIGHT_DATE", required = true)
    protected String flightdate;
    @XmlElement(name = "ACTUAL_DATE", required = true)
    protected String actualdate;
    @XmlElement(name = "TAIL_NO", required = true)
    protected String tailno;
    @XmlElement(name = "ORIGIN", required = true)
    protected String origin;
    @XmlElement(name = "DESTINATION", required = true)
    protected String destination;
    @XmlElement(name = "FLT_KIND", required = true)
    protected String fltkind;
    @XmlElement(name = "LEG")
    protected List<LEGType> leg;

    /**
     * Gets the value of the carrier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCARRIER() {
        return carrier;
    }

    /**
     * Sets the value of the carrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCARRIER(String value) {
        this.carrier = value;
    }

    /**
     * Gets the value of the flightno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFLIGHTNO() {
        return flightno;
    }

    /**
     * Sets the value of the flightno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFLIGHTNO(String value) {
        this.flightno = value;
    }

    /**
     * Gets the value of the flightdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFLIGHTDATE() {
        return flightdate;
    }

    /**
     * Sets the value of the flightdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFLIGHTDATE(String value) {
        this.flightdate = value;
    }

    /**
     * Gets the value of the actualdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACTUALDATE() {
        return actualdate;
    }

    /**
     * Sets the value of the actualdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACTUALDATE(String value) {
        this.actualdate = value;
    }

    /**
     * Gets the value of the tailno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTAILNO() {
        return tailno;
    }

    /**
     * Sets the value of the tailno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTAILNO(String value) {
        this.tailno = value;
    }

    /**
     * Gets the value of the origin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORIGIN() {
        return origin;
    }

    /**
     * Sets the value of the origin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORIGIN(String value) {
        this.origin = value;
    }

    /**
     * Gets the value of the destination property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDESTINATION() {
        return destination;
    }

    /**
     * Sets the value of the destination property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDESTINATION(String value) {
        this.destination = value;
    }

    /**
     * Gets the value of the fltkind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFLTKIND() {
        return fltkind;
    }

    /**
     * Sets the value of the fltkind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFLTKIND(String value) {
        this.fltkind = value;
    }

    /**
     * Gets the value of the leg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the leg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLEG().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LEGType }
     * 
     * 
     */
    public List<LEGType> getLEG() {
        if (leg == null) {
            leg = new ArrayList<LEGType>();
        }
        return this.leg;
    }

}
