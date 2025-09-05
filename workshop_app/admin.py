from django.contrib import admin
from django.contrib.admin import AdminSite
from django.utils.html import format_html
from django.urls import path
from django.shortcuts import render
from django.db.models import Count
from django.utils import timezone
from datetime import timedelta

from .models import (
    Profile, WorkshopType, Workshop, Comment, Testimonial, Banner,
    WorkshopCategory, WorkshopRating, Notification, ChatMessage, 
    WorkshopSchedule, AttachmentFile
)

# Custom Admin Site Configuration
class WorkshopAdminSite(AdminSite):
    site_header = "Workshop Booking System"
    site_title = "Workshop Admin"
    index_title = "Welcome to Workshop Management Dashboard"
    site_url = "/"

    def get_urls(self):
        urls = super().get_urls()
        custom_urls = [
            path('dashboard/', self.admin_view(self.dashboard_view), name='dashboard'),
        ]
        return custom_urls + urls

    def dashboard_view(self, request):
        # Get statistics for dashboard
        total_workshops = Workshop.objects.count()
        pending_workshops = Workshop.objects.filter(status=0).count()
        accepted_workshops = Workshop.objects.filter(status=1).count()
        total_users = Profile.objects.count()
        instructors = Profile.objects.filter(position='instructor').count()
        coordinators = Profile.objects.filter(position='coordinator').count()
        
        # Recent workshops
        recent_workshops = Workshop.objects.select_related(
            'workshop_type', 'coordinator', 'instructor'
        ).order_by('-date')[:10]
        
        # Workshop types with counts
        workshop_types = WorkshopType.objects.annotate(
            workshop_count=Count('workshop')
        ).order_by('-workshop_count')[:5]
        
        context = {
            'total_workshops': total_workshops,
            'pending_workshops': pending_workshops,
            'accepted_workshops': accepted_workshops,
            'total_users': total_users,
            'instructors': instructors,
            'coordinators': coordinators,
            'recent_workshops': recent_workshops,
            'workshop_types': workshop_types,
        }
        return render(request, 'admin/dashboard.html', context)

# Create custom admin site instance
admin_site = WorkshopAdminSite(name='workshop_admin')

# Enhanced Profile Admin
@admin.register(Profile, site=admin_site)
class ProfileAdmin(admin.ModelAdmin):
    list_display = ('user_full_name', 'institute', 'department', 'position', 'state', 'is_email_verified', 'created_date')
    list_filter = ('position', 'department', 'state', 'is_email_verified')
    search_fields = ('user__first_name', 'user__last_name', 'user__email', 'institute')
    readonly_fields = ('created_date',)
    list_per_page = 25
    
    fieldsets = (
        ('Personal Information', {
            'fields': ('user', 'title', 'institute', 'department', 'phone_number')
        }),
        ('Role & Location', {
            'fields': ('position', 'location', 'state')
        }),
        ('Verification', {
            'fields': ('is_email_verified', 'activation_key', 'key_expiry_time')
        }),
        ('Additional Info', {
            'fields': ('how_did_you_hear_about_us',)
        }),
    )
    
    def user_full_name(self, obj):
        return f"{obj.user.get_full_name()} ({obj.user.email})"
    user_full_name.short_description = 'User'
    
    def created_date(self, obj):
        return obj.user.date_joined.strftime('%Y-%m-%d %H:%M')
    created_date.short_description = 'Joined'

# Enhanced Workshop Type Admin
@admin.register(WorkshopType, site=admin_site)
class WorkshopTypeAdmin(admin.ModelAdmin):
    list_display = ('name', 'category', 'duration', 'difficulty_level', 'workshop_count', 'certification_offered')
    list_filter = ('category', 'difficulty_level', 'certification_offered', 'duration')
    search_fields = ('name', 'description')
    list_per_page = 25
    
    fieldsets = (
        ('Basic Information', {
            'fields': ('name', 'description', 'category', 'duration')
        }),
        ('Workshop Details', {
            'fields': ('difficulty_level', 'prerequisites', 'learning_outcomes', 'materials_provided')
        }),
        ('Certification', {
            'fields': ('certification_offered',)
        }),
        ('Terms & Conditions', {
            'fields': ('terms_and_conditions',)
        }),
    )
    
    def workshop_count(self, obj):
        count = obj.workshop_set.count()
        if count > 0:
            return format_html('<span style="color: green; font-weight: bold;">{}</span>', count)
        return format_html('<span style="color: gray;">{}</span>', count)
    workshop_count.short_description = 'Workshops'

# Enhanced Workshop Admin
@admin.register(Workshop, site=admin_site)
class WorkshopAdmin(admin.ModelAdmin):
    list_display = ('workshop_type', 'coordinator_name', 'instructor_name', 'date', 'status_badge', 'created_date')
    list_filter = ('status', 'workshop_type__category', 'date', 'workshop_type__difficulty_level')
    search_fields = ('workshop_type__name', 'coordinator__first_name', 'coordinator__last_name', 'instructor__first_name', 'instructor__last_name')
    list_per_page = 25
    date_hierarchy = 'date'
    
    fieldsets = (
        ('Workshop Details', {
            'fields': ('workshop_type', 'date', 'status')
        }),
        ('Participants', {
            'fields': ('coordinator', 'instructor')
        }),
        ('Agreement', {
            'fields': ('tnc_accepted',)
        }),
    )
    
    def coordinator_name(self, obj):
        return f"{obj.coordinator.get_full_name()} ({obj.coordinator.profile.institute})"
    coordinator_name.short_description = 'Coordinator'
    
    def instructor_name(self, obj):
        if obj.instructor:
            return f"{obj.instructor.get_full_name()} ({obj.instructor.profile.institute})"
        return format_html('<span style="color: orange;">Not Assigned</span>')
    instructor_name.short_description = 'Instructor'
    
    def status_badge(self, obj):
        status_colors = {
            0: ('orange', 'Pending'),
            1: ('green', 'Accepted'),
            2: ('red', 'Rejected')
        }
        color, text = status_colors.get(obj.status, ('gray', 'Unknown'))
        return format_html('<span style="background-color: {}; color: white; padding: 3px 8px; border-radius: 12px; font-size: 11px;">{}</span>', color, text)
    status_badge.short_description = 'Status'
    
    def created_date(self, obj):
        return obj.date.strftime('%Y-%m-%d')
    created_date.short_description = 'Workshop Date'

# Workshop Category Admin
@admin.register(WorkshopCategory, site=admin_site)
class WorkshopCategoryAdmin(admin.ModelAdmin):
    list_display = ('name', 'workshop_type_count', 'color_preview')
    search_fields = ('name', 'description')
    
    def workshop_type_count(self, obj):
        count = obj.workshoptype_set.count()
        return format_html('<span style="color: blue; font-weight: bold;">{}</span>', count)
    workshop_type_count.short_description = 'Workshop Types'
    
    def color_preview(self, obj):
        return format_html('<div style="width: 20px; height: 20px; background-color: {}; border-radius: 3px; display: inline-block;"></div>', obj.color)
    color_preview.short_description = 'Color'

# Workshop Rating Admin
@admin.register(WorkshopRating, site=admin_site)
class WorkshopRatingAdmin(admin.ModelAdmin):
    list_display = ('workshop', 'user_name', 'rating_stars', 'created_date')
    list_filter = ('rating', 'created_date')
    search_fields = ('workshop__workshop_type__name', 'user__first_name', 'user__last_name')
    
    def user_name(self, obj):
        return obj.user.get_full_name()
    user_name.short_description = 'User'
    
    def rating_stars(self, obj):
        stars = '★' * obj.rating + '☆' * (5 - obj.rating)
        return format_html('<span style="color: gold; font-size: 16px;">{}</span>', stars)
    rating_stars.short_description = 'Rating'

# Notification Admin
@admin.register(Notification, site=admin_site)
class NotificationAdmin(admin.ModelAdmin):
    list_display = ('user_name', 'notification_type', 'title', 'is_read', 'created_date')
    list_filter = ('notification_type', 'is_read', 'created_date')
    search_fields = ('user__first_name', 'user__last_name', 'title', 'message')
    readonly_fields = ('created_date',)
    
    def user_name(self, obj):
        return obj.user.get_full_name()
    user_name.short_description = 'User'

# Chat Message Admin
@admin.register(ChatMessage, site=admin_site)
class ChatMessageAdmin(admin.ModelAdmin):
    list_display = ('sender_name', 'receiver_name', 'workshop', 'message_preview', 'is_read', 'created_date')
    list_filter = ('is_read', 'created_date')
    search_fields = ('sender__first_name', 'sender__last_name', 'receiver__first_name', 'receiver__last_name', 'message')
    readonly_fields = ('created_date',)
    
    def sender_name(self, obj):
        return obj.sender.get_full_name()
    sender_name.short_description = 'From'
    
    def receiver_name(self, obj):
        return obj.receiver.get_full_name()
    receiver_name.short_description = 'To'
    
    def message_preview(self, obj):
        return obj.message[:50] + '...' if len(obj.message) > 50 else obj.message
    message_preview.short_description = 'Message'

# Comment Admin
@admin.register(Comment, site=admin_site)
class CommentAdmin(admin.ModelAdmin):
    list_display = ('author_name', 'workshop', 'comment_preview', 'public', 'created_date')
    list_filter = ('public', 'created_date')
    search_fields = ('author__first_name', 'author__last_name', 'comment')
    readonly_fields = ('created_date',)
    
    def author_name(self, obj):
        return obj.author.get_full_name()
    author_name.short_description = 'Author'
    
    def comment_preview(self, obj):
        return obj.comment[:50] + '...' if len(obj.comment) > 50 else obj.comment
    comment_preview.short_description = 'Comment'

# Testimonial Admin
@admin.register(Testimonial, site=admin_site)
class TestimonialAdmin(admin.ModelAdmin):
    list_display = ('name', 'institute', 'department', 'message_preview')
    search_fields = ('name', 'institute', 'department', 'message')
    
    def message_preview(self, obj):
        return obj.message[:50] + '...' if len(obj.message) > 50 else obj.message
    message_preview.short_description = 'Testimonial'

# Banner Admin
@admin.register(Banner, site=admin_site)
class BannerAdmin(admin.ModelAdmin):
    list_display = ('title', 'active', 'preview')
    list_filter = ('active',)
    
    def preview(self, obj):
        return format_html('<div style="max-width: 200px; max-height: 100px; overflow: hidden; border: 1px solid #ccc;">{}</div>', obj.html[:100])
    preview.short_description = 'Preview'

# Attachment File Admin
@admin.register(AttachmentFile, site=admin_site)
class AttachmentFileAdmin(admin.ModelAdmin):
    list_display = ('workshop_type', 'file_name', 'file_size')
    list_filter = ('workshop_type',)
    search_fields = ('workshop_type__name', 'attachments')
    
    def file_name(self, obj):
        return obj.attachments.name.split('/')[-1]
    file_name.short_description = 'File Name'
    
    def file_size(self, obj):
        try:
            size = obj.attachments.size
            if size < 1024:
                return f"{size} B"
            elif size < 1024 * 1024:
                return f"{size / 1024:.1f} KB"
            else:
                return f"{size / (1024 * 1024):.1f} MB"
        except:
            return "Unknown"
    file_size.short_description = 'Size'

# Workshop Schedule Admin
@admin.register(WorkshopSchedule, site=admin_site)
class WorkshopScheduleAdmin(admin.ModelAdmin):
    list_display = ('workshop', 'start_time', 'end_time', 'max_participants', 'venue_preview')
    search_fields = ('workshop__workshop_type__name', 'venue_details')
    
    def venue_preview(self, obj):
        return obj.venue_details[:30] + '...' if len(obj.venue_details) > 30 else obj.venue_details
    venue_preview.short_description = 'Venue'

# Register the custom admin site
admin.site = admin_site