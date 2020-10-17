package main.com.iglobuser.paymentclasses;

/**
 * Created by technorizen on 10/8/18.
 */

public class CardBean {
    String id;
    String member_id;
    String card_name;
    String card_number;
    String expiry_date;
    String expiry_year;
    String created_date;
    String card_type;

    String object;
    String brand;
    String country;
    String customer;
    String cvc_check;
    String dynamic_last4;
    String exp_month;
    String exp_year;
    String fingerprint;
    String funding;
    String last4;
    String setfullexpyearmonth;
    String setfullcardnumber;
    String defaultcard;
    boolean isDefaultCard;

    public boolean isDefaultCard() {
        return isDefaultCard;
    }

    public void setDefaultCard(boolean defaultCard) {
        isDefaultCard = defaultCard;
    }

    public String getDefaultcard() {
        return defaultcard;
    }

    public void setDefaultcard(String defaultcard) {
        this.defaultcard = defaultcard;
    }

    public String getSetfullexpyearmonth() {
        return setfullexpyearmonth;
    }

    public void setSetfullexpyearmonth(String setfullexpyearmonth) {
        this.setfullexpyearmonth = setfullexpyearmonth;
    }

    public String getSetfullcardnumber() {
        return setfullcardnumber;
    }

    public void setSetfullcardnumber(String setfullcardnumber) {
        this.setfullcardnumber = setfullcardnumber;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCvc_check() {
        return cvc_check;
    }

    public void setCvc_check(String cvc_check) {
        this.cvc_check = cvc_check;
    }

    public String getDynamic_last4() {
        return dynamic_last4;
    }

    public void setDynamic_last4(String dynamic_last4) {
        this.dynamic_last4 = dynamic_last4;
    }

    public String getExp_month() {
        return exp_month;
    }

    public void setExp_month(String exp_month) {
        this.exp_month = exp_month;
    }

    public String getExp_year() {
        return exp_year;
    }

    public void setExp_year(String exp_year) {
        this.exp_year = exp_year;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getExpiry_year() {
        return expiry_year;
    }

    public void setExpiry_year(String expiry_year) {
        this.expiry_year = expiry_year;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getCardtype() {
        return card_type;
    }

    public void setCardtype(String cardtype) {
        this.card_type = cardtype;
    }
}
