package com.news.recommend;

import com.news.recommend.Toneo4j.BNews;
import com.news.recommend.Toneo4j.JsonRead;
import com.news.recommend.dao.NewsRepository;
import com.news.recommend.dao.TagRepository;
import com.news.recommend.dao.UserRepository;
import com.news.recommend.entity.*;
import com.news.util.IdGen;
import com.news.util.PrefWordsGen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecommendApplicationTests {

    @Autowired
    private TagRepository tagRepository;

	@Autowired
	private NewsRepository newsRepository;

	@Autowired
	private UserRepository userRepository;

	/*@Test
	public void addTag() {
		Tag tag = new Tag("体育");
		*//*猜测默认的news null 里面存在关系*//*
		News anew = new News("体育大新闻","龟龟受伤",new Date());

        tag.addNew(anew);

        tagRepository.save(tag);
	}*/


	/*分词效果显示*/
	/*@Test
	public void jiebafenci() {
		JiebaSegmenter segmenter = new JiebaSegmenter();
		String[] sentences =
				new String[] {"这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。", "我不喜欢日本和服。", "雷猴回归人间。",
						"工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作", "结果婚的和尚未结过婚的"};
		for (String sentence : sentences) {
			System.out.println(segmenter.process(sentence, JiebaSegmenter.SegMode.INDEX).toString());
		}
	}*/


	public void addNewsFun(List<BNews> bNews) {
		SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
		String regEx = "^[0-9]\\d*$";
		Pattern pattern = Pattern.compile(regEx);
		for (BNews news : bNews) {
			if (!"".equals(news.getId()) && pattern.matcher(news.getId()).find()) {
				try {
					News anew = new News(news.getId(), news.getTitle(), news.getContent(), sdf.parse(news.getNewsDate()),news.getNewsTime(), (int) (Math.random()*100));
					newsRepository.save(anew);
					Tag tagmian = tagRepository.findTagByTagName(news.getCategory());
					if (tagmian != null) {
						TagToNew tagToNew = new TagToNew(1, tagmian, anew);
						anew.addTagToNew(tagToNew);
					} else {
						Tag t = new Tag(news.getCategory(),1);
						tagRepository.save(t);

						TagToNew tagToNew = new TagToNew(1, t, anew);
						anew.addTagToNew(tagToNew);
					}

					for (String s : news.getTag()) {
						Tag tagKeyword = tagRepository.findTagByTagName(s);
						if (tagKeyword != null) {
							TagToNew tagToNew = new TagToNew(2, tagKeyword, anew);
							anew.addTagToNew(tagToNew);
						} else {
							Tag t = new Tag(s,2);
							tagRepository.save(t);
							TagToNew tagToNew = new TagToNew(2, t, anew);
							anew.addTagToNew(tagToNew);
						}
					}
					newsRepository.save(anew);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@Test
	public void addNews() {
		List<BNews> bNews1 = JsonRead.read2("F:\\file\\娱乐-1");
		addNewsFun(bNews1);
		List<BNews> bNews2 = JsonRead.read2("F:\\file\\证券-1");
		addNewsFun(bNews2);
//		List<BNews> bNews3 = JsonRead.read2("F:\\file\\文化-1");
//		addNewsFun(bNews3);

	}

	/*@Test
	public void test_IF_IDF() {
		String content="孩子上了幼儿园 安全防拐教育要做好";
		int topN=5;
	TFIDFAnalyzer tfidfAnalyzer=new TFIDFAnalyzer();
	List<Keyword> list=tfidfAnalyzer.analyze(content,topN);
		for(Keyword word:list)
			System.out.println(word.getName()+":"+word.getTfidfvalue()+",");
	// 防拐:0.1992,幼儿园:0.1434,做好:0.1065,教育:0.0946,安全:0.0924
}*/

	@Test
	public void testCheck() {
		Iterable<News> news = newsRepository.findAll();
		Iterator<News> newIterator = news.iterator();
		while (newIterator.hasNext()) {
			News aa = newIterator.next();
			System.out.println(aa);
		}
	}

	@Test
	public void testDelete() {
		tagRepository.deleteAll();
		newsRepository.deleteAll();
	}

	@Test
	public void testUpdate() {
		newsRepository.updateNewsDate("8771757", "2019-04-04");
		newsRepository.updateNewsDate("8771450", "2019-04-03");
	}


	@Test
	public void testFind() {
		//时间段最热新闻
//		Iterable<News> news = newsRepository.findHotNewsBetween("2019-03-07","2019-04-04",0, 10);
		//新闻相似度最高
//		Iterable<News> news = newsRepository.findMostFamiliarNewsList("8771505",10);
//		for (News a:news
//		) {
//			System.out.println(a);
//		}
	}

	@Test
	public void testUserModel() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		User user = new User(IdGen.get().nextId(),"小红", "123457", "123457", sdf.format(new Date()));
//		userRepository.save(user);
		PrefWord prefWord = new PrefWord("体育", 2.0);
		PrefWord prefWord1 = new PrefWord("娱乐", 3.0);
		PrefWordsGen prefWordsGen = new PrefWordsGen();
//		prefWordsGen.addPrefWord(prefWord);
//		prefWordsGen.addPrefWord(prefWord1);
		user.setPrefWords(prefWordsGen.getPrefWord());
		userRepository.save(user);
	}

	@Test
	public void testDeleteUser() {
		userRepository.deleteAll();
//		User user = userRepository.findByUid(Long.valueOf("1103197065002876928"));
//		System.out.println(user.getId());
	}

	@Test
	public void testUserRead() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Optional<News> news = newsRepository.findByNid("8771991");
//		User user = userRepository.findByUid(Long.valueOf("1103197065002876928"));
		if (news.isPresent()) {
			News news1 = news.get();
//			System.out.println(news1);
//			NewsLog newsLog = new NewsLog(sdf.format(new Date()),news1,user);
//			news1.addNewsLog(newsLog);
			news1.setReadNum(news1.getReadNum()+1);
			newsRepository.save(news1);
		}
	}

	@Test
	public void testUserAttend() {
		String l = newsRepository.exitsNewsLog(Long.valueOf("110319706500287692"), "8771991");
		System.out.println(l);
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Optional<User> optionalUser = userRepository.findByUid(Long.valueOf("1103197065002876928"));
		Optional<User> optionalUser1 = userRepository.findByUid(Long.valueOf("1103199163950039040"));
		if (optionalUser.isPresent() && optionalUser1.isPresent()) {
			User user = optionalUser.get();
			User user1 = optionalUser1.get();
			Attention attention = new Attention(sdf.format(new Date()), user, user1);
			user.addAttention(attention);
			userRepository.save(user);
		}*/
	}

	/*@Test
	public void testLatest() {
		Sort sort = new Sort(Sort.Direction.ASC, "news.date").and(new Sort(Sort.Direction.DESC, "news.readNum"));
		Pageable pageable = PageRequest.of(0, 100,sort);
		Page<News> page = newsRepository.findAll(pageable);
		for (News n:page.getContent()
			 ) {
			System.out.println(n.getDate()+"----"+n.getReadNum()+"----"+n.getNid());
		}
	}*/
}

