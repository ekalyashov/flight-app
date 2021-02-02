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
 * <p>Java class for CREW_TASKType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CREW_TASKType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MEMBER_NR" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="DIVISION" type="{}DIVISIONType"/>
 *         &lt;element name="MEMBERS" type="{}MEMBERSType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CREW_TASKType", propOrder = {
    "no",
    "date",
    "type",
    "membernr",
    "division",
    "members"
})
public class CREWTASKType {

    @XmlElement(name = "NO", required = true)
    protected String no;
    @XmlElement(name = "DATE", required = true)
    protected String date;
    @XmlElement(name = "TYPE", required = true)
    protected String type;
    @XmlElement(name = "MEMBER_NR")
    protected short membernr;
    @XmlElement(name = "DIVISION", required = true)
    protected DIVISIONType division;
    @XmlElement(name = "MEMBERS")
    protected List<MEMBERSType> members;

    /**
     * Gets the value of the no property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNO() {
        return no;
    }

    /**
     * Sets the value of the no property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNO(String value) {
        this.no = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDATE() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDATE(String value) {
        this.date = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTYPE() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTYPE(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the membernr property.
     * 
     */
    public short getMEMBERNR() {
        return membernr;
    }

    /**
     * Sets the value of the membernr property.
     * 
     */
    public void setMEMBERNR(short value) {
        this.membernr = value;
    }

    /**
     * Gets the value of the division property.
     * 
     * @return
     *     possible object is
     *     {@link DIVISIONType }
     *     
     */
    public DIVISIONType getDIVISION() {
        return division;
    }

    /**
     * Sets the value of the division property.
     * 
     * @param value
     *     allowed object is
     *     {@link DIVISIONType }
     *     
     */
    public void setDIVISION(DIVISIONType value) {
        this.division = value;
    }

    /**
     * Gets the value of the members property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the members property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMEMBERS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MEMBERSType }
     * 
     * 
     */
    public List<MEMBERSType> getMEMBERS() {
        if (members == null) {
            members = new ArrayList<MEMBERSType>();
        }
        return this.members;
    }

}