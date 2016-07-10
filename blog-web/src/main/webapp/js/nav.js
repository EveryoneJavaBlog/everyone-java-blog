/**
 * @author iCodingStar
 * @date 2016/6/11 0:44
 * @version 1.0
 */
$(function () {
    //打开网站时加载技术类文章并显示出来
    var url = contextPath + "/HomePage/TopThreeUserArticles";
    var categoryName = "技术";
    getDataAndParse(url, {categoryName: categoryName});
    //根据用户对菜单的选择显示相应的最多评论、最多推荐、最多阅读的文章
    $(".blog-nav").on("click", "a", function (event) {
        $(".blog-nav a").removeClass("active");
        $(this).addClass("active");
        var id = $(this).attr("id");
        var content = $(this).text();
        if (id == "api") {
            window.location.href = contextPath + "/html/api-index.html";
        } else if (id == "technology" || id == "work" || id == "life") {
            categoryName = content;
            getDataAndParse(url, {categoryName: categoryName});
        }
    });
})

/**
 * @author iCodingStar
 * @date 2016/6/11 0:46
 * @version 1.0
 * @description: 根据url和data向服务器发送请求,并显示数据
 */
function getDataAndParse(url, data) {
    $.ajax({
        url: url,
        dataType: "json",
        data: data,
        type: "POST",
        cache: false,
        success: function (result) {
            $("#blog-rank ul li").remove();
            var mostRecommendArticle = result.data.mostRecommendArticle;
            var mostCommentArticle = result.data.mostCommentArticle;
            var mostReadArticle = result.data.mostReadArticle;
            var node = "";
            if (mostRecommendArticle != undefined) {
                node = "<li id='most-recommend-article'>[最多推荐]<a href='" + contextPath + "/Article/"+ mostRecommendArticle.id + "'>" + mostRecommendArticle.title + "</a></li>";
            }
            if (mostCommentArticle != undefined) {
                node += "<li id='most-commend-article'>[最多评论]<a href='" + contextPath + "/Article/"+ mostCommentArticle.id + "'>" + mostCommentArticle.title + "</a></li>";
            }
            if (mostReadArticle != undefined) {
                node += "<li id='most-read-article'>[最多阅读]<a href='" + contextPath + "/Article/"+ mostReadArticle.id + "'>" + mostReadArticle.title + "</a></li>";
            }
            $("#blog-rank ul").append(node);
        },
        error: function () {
            console.log("获取文章排名失败！");
        }
    });
}