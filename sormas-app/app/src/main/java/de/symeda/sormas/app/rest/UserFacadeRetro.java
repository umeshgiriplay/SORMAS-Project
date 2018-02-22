package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.user.UserDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface UserFacadeRetro {

    @GET("users/all/{since}")
    Call<List<UserDto>> pullAllSince(@Path("since") long since);

    @GET("tasks/query")
    Call<List<UserDto>> pullByUuids(@Query("uuids") List<String> uuids);

    @GET("users/uuids")
    Call<List<String>> pullUuids();
}
