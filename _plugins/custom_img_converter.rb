module Jekyll
  class CustomImgConverter < Jekyll::Converters::Markdown
    def convert(content)
      # 使用正則表達式匹配 shields.io 的圖片網址
      content = content.gsub(/<a href="(https:\/\/img\.shields\.io\/.+?)".*?><img src="(https:\/\/img\.shields\.io\/.+?)".*?><\/a>/) do
        # 只保留圖片的 <img> 標籤
        "<img src='#{Regexp.last_match(2)}' alt='shields badge' loading='lazy'>"
      end

      # 調用原始 Markdown 渲染邏輯處理其他內容
      super(content)
    end
  end
end
