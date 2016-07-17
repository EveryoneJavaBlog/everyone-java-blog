/*
 * Copyright 2002-2016 the original author or authors.
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
package com.zuoxiaolong.blog.web.controller;

import com.zuoxiaolong.blog.common.bean.JsonResponse;
import com.zuoxiaolong.blog.common.utils.CollectionUtils;
import com.zuoxiaolong.blog.model.persistent.ArticleComment;
import com.zuoxiaolong.blog.model.persistent.UserArticle;
import com.zuoxiaolong.blog.sdk.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;


/**
 * @author DeserveL
 * @date 2016/6/11 18:20
 * @since 1.0.0
 */
@Controller
@RequestMapping("/Article")
public class ArticleController extends AbstractWebController {

    /**
     * 查看文章详细
     *
     * @param articleid 文章id
     * @return
     */
    @RequestMapping(value = "/{articleid}")
    public String getArticleInfo(@PathVariable int articleid) {
        JsonResponse response = invokeApi(Api.Article_GetArticleInfo, CollectionUtils.newMap("articleid", articleid + ""));
        if(response.getCode() == 200){
            setModelAttribute("result", response);
            return "article/article";
        }
        return "/index/index";
    }

    @RequestMapping(value = "/Write")
    public String writeIndex(){
        JsonResponse articleList = invokeApi(Api.Article_GetUserArticle);
        if(articleList.success()){
            setModelAttribute("UserArticles",articleList.getData());
        }
        return "/write/index";
    }
    @RequestMapping(value = "/Write/{articleid}")
    public String getArticleById(@PathVariable int articleid) {
        JsonResponse response = invokeApi(Api.Article_GetArticleInfo, CollectionUtils.newMap("articleid", articleid + ""));
        JsonResponse articleList = invokeApi(Api.Article_GetUserArticle);
        if(articleList.success()){
            setModelAttribute("UserArticles",articleList.getData());
        }
        if(response.success()){
            setModelAttribute("ArticleInfoDTO", response.getData());
        }
        return "/write/index";
    }
    /**
     * 查看评论和每条评论前三条回复列表
     *
     * @param articleid 文章id
     * @param offset 分页开始头评论id
     * @param size 每次加载数
     */
    @RequestMapping(value = "/GetCommentInfo" , method = RequestMethod.GET)
    public void getCommentInfo(int articleid,int offset, int size) {
        Map<String, String> params = new HashMap<>();
        params.put("articleid", articleid + "");
        params.put("offset", offset + "");
        params.put("size", size + "");
        JsonResponse response = invokeApi(Api.Article_GetCommentInfo,params);
        renderJson(response);
    }


    /**
     *加载该条评论的更多回复
     *
     * @param commondid 评论id
     * @param offset 分页开始头评论id
     * @param size 每次加载数
     */
    @RequestMapping(value = "/GetMoreReComment" , method = RequestMethod.GET)
    public void getMoreReComment(int commondid,int offset, int size){
        Map<String, String> params = new HashMap<>();
        params.put("commondid", commondid + "");
        params.put("offset", offset + "");
        params.put("size", size + "");
        JsonResponse response = invokeApi(Api.Article_GetMoreReComment, params);
        renderJson(response);
    }

    /**
     * 添加评论（回复）
     *
     * @param articleComment
     */
    @RequestMapping(value = "/AddComment" , method = RequestMethod.POST)
    public void AddComment(ArticleComment articleComment){
        JsonResponse response = invokeApi(Api.Article_AddComment, articleComment);
        renderJson(response);
    }

    /**
     * 添加一次点赞
     *
     * @param articleid
     */
    @RequestMapping(value = "/AddThumbupTimes" , method = RequestMethod.POST)
    public void addThumbupTimes(int articleid){
        JsonResponse response = invokeApi(Api.Article_AddThumbupTimes, CollectionUtils.newMap("articleid", articleid + ""));
        renderJson(response);
    }

    /**
     * 增加一篇博文
     * @param userArticle
     */
    @RequestMapping(value = "/Add/UserArticle" , method = RequestMethod.POST)
    public void addUserArticle(UserArticle userArticle){
        JsonResponse response = invokeApi(Api.Article_AddUserArticle,userArticle);
        renderJson(response);
    }

    /**
     *修改博文信息（标题、内容、状态）
     */
    @RequestMapping(value = "/UpdUserArticle" , method = RequestMethod.POST)
    public void updUserArticle(UserArticle userArticle){
        JsonResponse response = invokeApi(Api.Article_UpdUserArticle,userArticle);
        renderJson(response);
    }

    /**
     *获取当前用户对应的文章
     */
    @RequestMapping(value = "/GetUserArticle" , method = RequestMethod.GET)
    public void getUserArticle(){
        JsonResponse response = invokeApi(Api.Article_GetUserArticle);
        renderJson(response);
    }
}
