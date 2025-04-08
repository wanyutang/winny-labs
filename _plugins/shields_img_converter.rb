# _plugins/shields_img_converter.rb

module Jekyll
  Jekyll::Hooks.register :site, :post_write do |site|
    Dir.glob("_site/**/*.html").each do |file|
      content = File.read(file)
      # 只移除含 shields 圖片的 <a> 的 popup class
      content.gsub!(/<a([^>]*)class="([^"]*)popup([^"]*)"([^>]*)>\s*<img[^>]*src="https:\/\/img\.shields\.io\/[^"]+"[^>]*>/) do
        other_classes = [Regexp.last_match(2), Regexp.last_match(3)].join.strip
        other_classes = other_classes.gsub(/\s+/, ' ') # 去掉多餘空白
        other_classes = other_classes.empty? ? '' : " class=\"#{other_classes}\""
        "<a#{Regexp.last_match(1)}#{other_classes}#{Regexp.last_match(4)}>"
      end
      File.write(file, content)
    end
  end
end
