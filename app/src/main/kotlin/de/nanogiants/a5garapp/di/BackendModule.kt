package de.nanogiants.a5garapp.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.nanogiants.a5garapp.BuildConfig
import de.nanogiants.a5garapp.model.datasource.api.POIDatasource
import de.nanogiants.a5garapp.model.datasource.api.TagDatasource
import de.nanogiants.a5garapp.model.datastore.POIDatastore
import de.nanogiants.a5garapp.model.datastore.POIDatastoreImpl
import de.nanogiants.a5garapp.model.datastore.ReviewDatastore
import de.nanogiants.a5garapp.model.datastore.ReviewDatastoreImpl
import de.nanogiants.a5garapp.model.datastore.TagDatastore
import de.nanogiants.a5garapp.model.datastore.TagDatastoreImpl
import de.nanogiants.a5garapp.model.transformer.POIWebTransformer
import de.nanogiants.a5garapp.model.transformer.POIWebTransformerImpl
import de.nanogiants.a5garapp.model.transformer.TagWebTransformer
import de.nanogiants.a5garapp.model.transformer.TagWebTransformerImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import okhttp3.logging.HttpLoggingInterceptor.Logger
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber


@Module(includes = [BackendStaticModule::class])
@InstallIn(ApplicationComponent::class)
abstract class BackendModule {

  @Binds
  abstract fun providePOIDatastore(poiDatastoreImpl: POIDatastoreImpl): POIDatastore

  @Binds
  abstract fun provideTagDataStore(tagDatastoreImpl: TagDatastoreImpl): TagDatastore

  @Binds
  abstract fun provideReviewDataStore(reviewDatastoreImpl: ReviewDatastoreImpl): ReviewDatastore

  @Binds
  abstract fun providePOIWebTransformer(poiWebTransformerImpl: POIWebTransformerImpl): POIWebTransformer

  @Binds
  abstract fun provideTagWebTransformer(tagWebTransformerImpl: TagWebTransformerImpl): TagWebTransformer
}

@Module
@InstallIn(ApplicationComponent::class)
object BackendStaticModule {

  @Provides
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .addNetworkInterceptor(HttpLoggingInterceptor(object : Logger {
        override fun log(message: String) {
          Timber.tag("OkHttp").d(message)
        }
      }).setLevel(if (BuildConfig.DEBUG) Level.BODY else Level.NONE))
      .build()
  }

  @Provides
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
  }

  @Provides
  fun providePOIDatasource(retrofit: Retrofit): POIDatasource =
    retrofit.create(POIDatasource::class.java)

  @Provides
  fun provideTagDatasource(retrofit: Retrofit): TagDatasource =
    retrofit.create(TagDatasource::class.java)
}