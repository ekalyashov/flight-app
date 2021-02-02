package cselp.domain.external;


import java.io.Serializable;
import java.sql.Timestamp;

public class Parameter implements Serializable {

    private Long id;
    private Short typeId;
    private Long filterTypeId;
    private Long parentId;
    private Short formatId;
    private Long parameterNumber;
    private String mnemonic;
    private String name;
    private String description;
    private String unit;
    private String positiveSign;
    private String negativeSign;
    private Short fieldSize;
    private Short decimalPlaces;
    private Double rate;
    private Double minOpRange;
    private Double maxOpRange;
    private Long timeOffset;
    private String family;
    private Boolean ssm;
    private Boolean statical;
    private String plotlistDefaultDisplayFormat;
    private Short plotlistDefaultGraphicFormat;
    private String plotlistHeader1;
    private String plotlistHeader2;
    private String plotlistHeader3;
    private Timestamp updateDate;
    private String state;
    private Boolean displayLeadingZeros;
    private Double filterArgument1;
    private Double filterArgument2;
    private Short validityDisplay;
    private Boolean parameterSigned;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getTypeId() {
        return typeId;
    }

    public void setTypeId(Short typeId) {
        this.typeId = typeId;
    }

    public Long getFilterTypeId() {
        return filterTypeId;
    }

    public void setFilterTypeId(Long filterTypeId) {
        this.filterTypeId = filterTypeId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Short getFormatId() {
        return formatId;
    }

    public void setFormatId(Short formatId) {
        this.formatId = formatId;
    }

    public Long getParameterNumber() {
        return parameterNumber;
    }

    public void setParameterNumber(Long parameterNumber) {
        this.parameterNumber = parameterNumber;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPositiveSign() {
        return positiveSign;
    }

    public void setPositiveSign(String positiveSign) {
        this.positiveSign = positiveSign;
    }

    public String getNegativeSign() {
        return negativeSign;
    }

    public void setNegativeSign(String negativeSign) {
        this.negativeSign = negativeSign;
    }

    public Short getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(Short fieldSize) {
        this.fieldSize = fieldSize;
    }

    public Short getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Short decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getMinOpRange() {
        return minOpRange;
    }

    public void setMinOpRange(Double minOpRange) {
        this.minOpRange = minOpRange;
    }

    public Double getMaxOpRange() {
        return maxOpRange;
    }

    public void setMaxOpRange(Double maxOpRange) {
        this.maxOpRange = maxOpRange;
    }

    public Long getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(Long timeOffset) {
        this.timeOffset = timeOffset;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Boolean getSsm() {
        return ssm;
    }

    public void setSsm(Boolean ssm) {
        this.ssm = ssm;
    }

    public Boolean getStatical() {
        return statical;
    }

    public void setStatical(Boolean statical) {
        this.statical = statical;
    }

    public String getPlotlistDefaultDisplayFormat() {
        return plotlistDefaultDisplayFormat;
    }

    public void setPlotlistDefaultDisplayFormat(String plotlistDefaultDisplayFormat) {
        this.plotlistDefaultDisplayFormat = plotlistDefaultDisplayFormat;
    }

    public Short getPlotlistDefaultGraphicFormat() {
        return plotlistDefaultGraphicFormat;
    }

    public void setPlotlistDefaultGraphicFormat(Short plotlistDefaultGraphicFormat) {
        this.plotlistDefaultGraphicFormat = plotlistDefaultGraphicFormat;
    }

    public String getPlotlistHeader1() {
        return plotlistHeader1;
    }

    public void setPlotlistHeader1(String plotlistHeader1) {
        this.plotlistHeader1 = plotlistHeader1;
    }

    public String getPlotlistHeader2() {
        return plotlistHeader2;
    }

    public void setPlotlistHeader2(String plotlistHeader2) {
        this.plotlistHeader2 = plotlistHeader2;
    }

    public String getPlotlistHeader3() {
        return plotlistHeader3;
    }

    public void setPlotlistHeader3(String plotlistHeader3) {
        this.plotlistHeader3 = plotlistHeader3;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getDisplayLeadingZeros() {
        return displayLeadingZeros;
    }

    public void setDisplayLeadingZeros(Boolean displayLeadingZeros) {
        this.displayLeadingZeros = displayLeadingZeros;
    }

    public Double getFilterArgument1() {
        return filterArgument1;
    }

    public void setFilterArgument1(Double filterArgument1) {
        this.filterArgument1 = filterArgument1;
    }

    public Double getFilterArgument2() {
        return filterArgument2;
    }

    public void setFilterArgument2(Double filterArgument2) {
        this.filterArgument2 = filterArgument2;
    }

    public Short getValidityDisplay() {
        return validityDisplay;
    }

    public void setValidityDisplay(Short validityDisplay) {
        this.validityDisplay = validityDisplay;
    }

    public Boolean getParameterSigned() {
        return parameterSigned;
    }

    public void setParameterSigned(Boolean parameterSigned) {
        this.parameterSigned = parameterSigned;
    }
}
