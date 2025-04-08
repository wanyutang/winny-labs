# _plugins/custom_img_converter.rb
module Jekyll
  class UnifiedMarkdownConverter < Jekyll::Converters::Markdown
    def convert(content)
      # 處理 `![[filename | width]]` 或 `![[filename]]` 特定格式
      content = content.gsub(/!\[\[(.+?)(?:\s*\|\s*(\d+))?\]\]/) do
        filename = Regexp.last_match(1).strip
        width = Regexp.last_match(2)
        if width
          "<img src='/assets/images/#{filename}' width='#{width}' alt='#{filename}' />"
        else
          "<img src='/assets/images/#{filename}' alt='#{filename}' />"
        end
      end

      # 處理 shields.io 的 Markdown 圖片
      content = content.gsub(/!\[.*?\]\((https:\/\/img\.shields\.io\/.+?)\)/) do
        url = Regexp.last_match(1)
        "<img src='#{url}' alt='shields badge' loading='lazy'>"
      end

      # 調用原始 Markdown 渲染邏輯處理其他內容
      super(content)
    end
  end
end
