# _plugins/custom_image_tag.rb
module Jekyll
  class CustomImageTag < Jekyll::Converters::Markdown
    def convert(content)
      # 將 `![[filename | width]]` 或 `![[filename]]` 轉換為 <img> 標籤
      content.gsub(/!\[\[(.+?)(?:\s*\|\s*(\d+))?\]\]/) do
        filename = Regexp.last_match(1).strip
        width = Regexp.last_match(2)
        if width
          "<img src='/assets/images/#{filename}' width='#{width}' alt='#{filename}' />"
        else
          "<img src='/assets/images/#{filename}' alt='#{filename}' />"
        end
      end
    end
  end
end
