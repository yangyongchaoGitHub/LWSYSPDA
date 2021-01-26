package com.dataexpo.lwsyspda.retrofitInf;

import com.dataexpo.lwsyspda.entity.Bom;
import com.dataexpo.lwsyspda.entity.BomHouseInfo;
import com.dataexpo.lwsyspda.entity.Device;
import com.dataexpo.lwsyspda.entity.NetResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.dataexpo.lwsyspda.retrofitInf.URLs.*;

public interface BomService {
    @GET(bomListUrl)
    Call<NetResult<List<Bom>>> getBomList(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize,
                                          @Query("keyWord") String keyWord, @Query("type") Integer type,
                                          @Query("status") Integer status);

    @GET(bomSeriesUrl)
    Call<NetResult<List<BomHouseInfo>>> getBomSeries(@Query("bomId") int bomId);

    @GET(bomDeviceUrl)
    Call<NetResult<List<Device>>> getBomDevice(@Query("bomId") int bomId);
}
