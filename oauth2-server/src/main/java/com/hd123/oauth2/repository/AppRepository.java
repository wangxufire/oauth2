/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hd123.oauth2.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.hd123.oauth2.entity.App;

/**
 * 应用持久层管理
 *
 * @author liyue
 */
public interface AppRepository extends MongoRepository<App, ObjectId> {

  Optional<App> findDistinctByAppId(String appId);

  Optional<App> findDistinctByAppSecret(String appSecret);

  Optional<App> findDistinctByAppName(String appName);

  App findDistinctByAccessToken(String accessToken);

}