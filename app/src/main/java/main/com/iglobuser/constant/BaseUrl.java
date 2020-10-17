package main.com.iglobuser.constant;

/**
 * Created by technorizen on 5/6/17.
 */

public class BaseUrl {
    public static String baseurl="https://iglobapp.com/admin/webservice/";
    public static String image_baseurl="https://iglobapp.com/admin/uploads/images/";
    public static String privacy="https://iglobapp.com/admin/privacy.php";
    public static String termsconditions="https://iglobapp.com/admin/terms.php";
    public static String stripe_publish="pk_test_3oQpHM18Yv2mFAK6vSE5I1oz";
    public static BaseUrl get() {
        return new BaseUrl();
    }
    public String sendWalletAmount() {
        return baseurl.concat("send_wallet_amount");
    }
    public String getVerify() {
        return baseurl.concat("get_number_profile");
    }public String getCarTypeList() {
        return baseurl.concat("get_car_type_list");
    }public String getAvailableCarDriver() {
        return baseurl.concat("available_car_driver");
    }public String cancelRide() {
        return baseurl.concat("cancel_ride");
    }public String getNearBranch() {
        return baseurl.concat("doctor_search");
    }
}