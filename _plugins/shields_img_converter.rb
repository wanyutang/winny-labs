# _plugins/shields_img_converter.rb

module Jekyll
  Jekyll::Hooks.register :documents, :post_render do |document|
    document.content = document.content.gsub(/<a href="(https:\/\/img\.shields\.io\/.+?)".*?><img src="(https:\/\/img\.shields\.io\/.+?)".*?><\/a>/) do
      url = Regexp.last_match(2)
      "<img src='#{url}' alt='shields badge' loading='lazy'>"
    end
  end
end
