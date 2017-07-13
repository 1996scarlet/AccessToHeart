package access.to.heart.Interface

import access.to.heart.Bean.Heart
import access.to.heart.Bean.ProfileUser
import access.to.heart.Bean.User
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/7/12.
 */
interface CloudAPI {

    @GET("user")
    fun getUser(@Query("offset") offset: Int = 0,
                @Query("query") query: String? = null,
                @Query("sortby") sortby: String? = null,
                @Query("order") order: String? = null): Observable<List<User>>

    //get User by id
    @GET("user/{id}")
    fun getUserById(@Path("id") userId: Int): Observable<User>

    //create User
    @POST("user")
    fun postUser(@Body user: User): Observable<String>

    //------------------------------------------------------------------------------//

    @GET("heart")
    fun getHeart(@Query("offset") offset: Int = 0,
                 @Query("query") query: String? = null,
                 @Query("sortby") sortby: String? = null,
                 @Query("order") order: String? = null): Observable<List<Heart>>

    @POST("heart")
    fun postHeart(@Body heart: Heart): Observable<String>

    @DELETE("heart/{id}")
    fun deleteHeartById(@Path("id") heartId: Int): Observable<String>

    //-----------------------------------------------------------------------------//

    @GET("profile_user")
    fun getProfile(@Query("offset") offset: Int = 0,
                   @Query("query") query: String? = null,
                   @Query("sortby") sortby: String? = null,
                   @Query("order") order: String? = null): Observable<List<ProfileUser>>

    @POST("profile_user")
    fun postProfile(@Body profileUser: ProfileUser): Observable<String>
}