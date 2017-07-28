package com.yundian.celebrity.networkapi;


import com.yundian.celebrity.bean.RequestResultBean;
import com.yundian.celebrity.listener.OnAPIListener;

/**
 * Created by ysl .
 * 信息获取
 *
 */
//Information接口,要求子类必须实现的方法
public interface InformationAPI {
//    void newsinfo(String name, String code,int startnum,int endnum,int all, OnAPIListener<NewsInforModel> listener);
//    void advInfo(String code,int all, OnAPIListener<AdvBeen> listener);
//    void searchStar(long id,String token ,String message,OnAPIListener<SearchReturnbeen> listener);
//    void getOptionsStarList(String phone,int startnum,int endnum,int sorttype,OnAPIListener<OptionsStarListBeen> listener);
//    void getMarketKype(String phone,OnAPIListener<MarketTypeBeen> listener);
//    void getStarList(long id,String token,int sort,int aType,int start,int count,OnAPIListener<StarListbeen> listener);
//    void getStarBrief(String code,OnAPIListener<StarBuyActReferralInfo> listener);
//    void getStarExperience(String code,OnAPIListener<StarExperienceBeen> listener);
//    void getStarachive(String code,OnAPIListener<StarStarAchBeen> listener);
//    void getSeekList(String code,int startnum,int endnum,OnAPIListener<FansHotBuyReturnBeen> listener);
//    void getTransferList(String code,int startnum,int endnum,OnAPIListener<FansHotBuyReturnBeen> listener);
//    void getFansComments(String starcode,OnAPIListener<Object> listener);
//    void getStarmaillist(long id ,String token,String status,int startPos,int count,OnAPIListener<StarMailListBeen> listener);
//    void addFriend(String accid,String faccid,String msg,int type,OnAPIListener<Object> listener);
//    void reduceTime(String phone,String starcode,long deduct_amount,OnAPIListener<Object> listener);
//    void inquiry(String symbol,int startPos,int count ,OnAPIListener<CommentMarketBeen> listener);
//    void getTimeLine(long id,String token,String symbol ,int aType,OnAPIListener<TimeLineBeen> listener);
//    void getAddComment(String symbol,String fans_id,String nick_name,String comments,String head_url ,OnAPIListener<Object> listener);
//    void getSrealtime(long id, String token, List<SrealSendBeen> symbolInfos , OnAPIListener<SrealSendReturnBeen> listener);
//    void getAskToBuy(long id, String token,int sort ,String symbol,int buySell,int amount,double price,OnAPIListener<AskToBuyReturnBeen> listener);
//    void getTradingStatus(long id, String token,String symbol,OnAPIListener<TradingStatusBeen> listener);
//    void cancelOrder(long id, String token,long orderId,OnAPIListener<OrderCancelReturnBeen> listener);
    void checkPayPas(long id, String token,String paypwd,OnAPIListener<RequestResultBean> listener);
//    void sureOrder(long id, String token,long orderId,long positionId,OnAPIListener<SureOrder> listener);
//
//    void theDayOrder(long id, String token,int status,int start,int count,OnAPIListener<OrderReturnBeen> listener);
//    void historyOrder(long id, String token,int status,int start,int count,OnAPIListener<OrderReturnBeen> listener);
//    void historyEntrust(long id, String token,int start,int count,OnAPIListener<EntrustReturnBeen> listener);
//    void oederFansList(long id, String token,String symbol,int buySell,int start,int count,OnAPIListener<FansTopListBeen> listener);
//
//    void todayEntrust(int start,int count,Short opcode,OnAPIListener<List<TodayEntrustReturnBean>> listener);
//    void todayDeal(int status,int start,int count,Short opcode,OnAPIListener<List<TodayDealReturnBean>> listener);
//    void fansRntrust(String symbol,int buySell, int start,int count,OnAPIListener<FansEntrustReturnBean> listener);
//
//    void starInfo(String phone,String code, int all,OnAPIListener<StarInfoReturnBean> listener);
//    void getHaveStarTime(long uid,String starcode,OnAPIListener<HaveStarTimeBeen> listener);
//    void getStarShellTime(String starcode,OnAPIListener<StartShellTimeBeen> listener);
//    void getBuyShellData(long id, String token,String symbol,OnAPIListener<BuyShellReutrnBeen> listener);
}
