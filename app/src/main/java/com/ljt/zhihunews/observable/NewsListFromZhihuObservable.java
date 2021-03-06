package com.ljt.zhihunews.observable;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.ljt.zhihunews.bean.DailyNews;
import com.ljt.zhihunews.bean.Question;
import com.ljt.zhihunews.bean.Story;
import com.ljt.zhihunews.support.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.ljt.zhihunews.observable.Helper.getHtml;
import static com.ljt.zhihunews.observable.Helper.toNonempty;

/**
 * Created by Administrator on 2017/10/7/007.
 */

public class NewsListFromZhihuObservable {
    public static String TAG=NewsListFromZhihuObservable .class.getSimpleName();
    private static final String QUESTION_SELECTOR = "div.question";
    private static final String QUESTION_TITLES_SELECTOR = "h2.question-title";
    private static final String QUESTION_LINKS_SELECTOR = "div.view-more a";

    public static Observable<List<DailyNews>> ofDate(String date) {
        /*
        * 层层嵌套，下一层依赖上一层
        * */
        Observable<Story> stories =
                //将url根据时间来编码，拿到json数据
                getHtml(Constants.Urls.ZHIHU_DAILY_BEFORE, date)
                 //获取jsonArray数据
                .flatMap(NewsListFromZhihuObservable::getStoriesJsonArrayObservable)
                  //解析json数据 拿到故事标题 图片 信息
                .flatMap(NewsListFromZhihuObservable::getStoriesObservable);
        Log.d(TAG,TAG+" "+"---->>>ofDate(String date)------->>> stories= "+stories);

        Observable<Document> documents = stories
                .flatMap(NewsListFromZhihuObservable::getDocumentObservable);

        Observable<Optional<Pair<Story, Document>>> optionalStoryNDocuments
                = Observable.zip(stories, documents, NewsListFromZhihuObservable::createPair);

        Observable<Pair<Story, Document>> storyNDocuments = toNonempty(optionalStoryNDocuments);
        Log.d(TAG,TAG+" "+" ---->>>-------------->>> storyNDocuments= "+storyNDocuments);
        return toNonempty(storyNDocuments.map(NewsListFromZhihuObservable::convertToDailyNews))
                .doOnNext(news -> news.setDate(date))
                .toList();
    }
/*
*
* 拿到某一天
* */
    private static Observable<JSONArray> getStoriesJsonArrayObservable(String html) {
        return Observable.create(subscriber -> {
            try {
                Log.d("NewsListFragment",TAG+"  getStoriesJsonArrayObservable(----->>>url= "+html.toString());
                JSONArray stories = new JSONObject(html).getJSONArray("stories");
                Log.d("NewsListFragment",TAG+"  getStoriesJsonArrayObservable(----->>>JSONArray stories" +
                        "= "+stories.toString());
                subscriber.onNext(new JSONObject(html).getJSONArray("stories"));
                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static Observable<Story> getStoriesObservable(JSONArray newsArray) {
        return Observable.create(subscriber -> {
            try {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject newsJson = newsArray.getJSONObject(i);
                    subscriber.onNext(getStoryFromJSON(newsJson));
                }

                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static Story getStoryFromJSON(JSONObject jsonStory) throws JSONException {
        Story story = new Story();

        story.setStoryId(jsonStory.getInt("id"));
        story.setDailyTitle(jsonStory.getString("title"));
        story.setThumbnailUrl(getThumbnailUrlForStory(jsonStory));

        return story;
    }

    private static String getThumbnailUrlForStory(JSONObject jsonStory) throws JSONException {
        if (jsonStory.has("images")) {
            return (String) jsonStory.getJSONArray("images").get(0);
        } else {
            return null;
        }
    }

    private static Observable<Document> getDocumentObservable(Story news) {
        return getHtml(Constants.Urls.ZHIHU_DAILY_OFFLINE_NEWS, news.getStoryId())
                .map(NewsListFromZhihuObservable::getStoryDocument);
    }

    private static Document getStoryDocument(String json) {
        try {
            JSONObject newsJson = new JSONObject(json);
            return newsJson.has("body") ? Jsoup.parse(newsJson.getString("body")) : null;
        } catch (JSONException e) {
            return null;
        }
    }

    private static Optional<Pair<Story, Document>> createPair(Story story, Document document) {
        return Optional.ofNullable(document == null ? null : Pair.create(story, document));
    }

    /*
    * Pair:对  泛型类
    * */
    private static Optional<DailyNews> convertToDailyNews(Pair<Story, Document> pair) {
        DailyNews result = null;

        Story story = pair.first;
        Document document = pair.second;
        String dailyTitle = story.getDailyTitle();

        List<Question> questions = getQuestions(document, dailyTitle);
        Log.d(TAG,TAG+"convertToDailyNews( ----->>>dailyTitle= "+ dailyTitle.toString()
                +"  convertToDailyNews( ----->>>questions= "+
                questions.get(0).getUrl());

        if (Stream.of(questions).allMatch(Question::isValidMyApplication)) {
            result = new DailyNews();

            result.setDailyTitle(dailyTitle);
            result.setThumbnailUrl(story.getThumbnailUrl());
            result.setQuestions(questions);
        }

        return Optional.ofNullable(result);
    }

    private static List<Question> getQuestions(Document document, String dailyTitle) {
        List<Question> result = new ArrayList<>();
        Elements questionElements = getQuestionElements(document);

        for (Element questionElement : questionElements) {
            Question question = new Question();

            String questionTitle = getQuestionTitleFromQuestionElement(questionElement);
            String questionUrl = getQuestionUrlFromQuestionElement(questionElement);
            // Make sure that the question's title is not empty.
            questionTitle = TextUtils.isEmpty(questionTitle) ? dailyTitle : questionTitle;

            question.setTitle(questionTitle);
            question.setUrl(questionUrl);

            result.add(question);
        }

        return result;
    }

    private static Elements getQuestionElements(Document document) {
        return document.select(QUESTION_SELECTOR);
    }

    private static String getQuestionTitleFromQuestionElement(Element questionElement) {
        Element questionTitleElement = questionElement.select(QUESTION_TITLES_SELECTOR).first();

        if (questionTitleElement == null) {
            return null;
        } else {
            return questionTitleElement.text();
        }
    }

    private static String getQuestionUrlFromQuestionElement(Element questionElement) {
        Element viewMoreElement = questionElement.select(QUESTION_LINKS_SELECTOR).first();

        if (viewMoreElement == null) {
            return null;
        } else {
            return viewMoreElement.attr("href");
        }
    }
}
