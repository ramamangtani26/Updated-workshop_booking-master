import os
import uuid
import pandas as pd

from django.contrib.auth.models import User
from django.core.validators import RegexValidator
from django.db import models
from django.utils import timezone
from django.core.validators import MinValueValidator

position_choices = (
    ("coordinator", "Coordinator"),
    ("instructor", "Instructor")
)

department_choices = (
    ("computer engineering", "Computer Science"),
    ("information technology", "Information Technology"),
    ("civil engineering", "Civil Engineering"),
    ("electrical engineering", "Electrical Engineering"),
    ("mechanical engineering", "Mechanical Engineering"),
    ("chemical engineering", "Chemical Engineering"),
    ("aerospace engineering", "Aerospace Engineering"),
    ("biosciences and bioengineering", "Biosciences and  BioEngineering"),
    ("electronics", "Electronics"),
    ("energy science and engineering", "Energy Science and Engineering"),
)

title = (
    ("Professor", "Prof."),
    ("Doctor", "Dr."),
    ("Shriman", "Shri"),
    ("Shrimati", "Smt"),
    ("Kumari", "Ku"),
    ("Mr", "Mr."),
    ("Mrs", "Mrs."),
    ("Miss", "Ms."),
)

source = (
    ("FOSSEE website", "FOSSEE website"),
    ("Google", "Google"),
    ("Social Media", "Social Media"),
    ("From other College", "From other College"),
)

states = (
    ("", "---------"),
    ("IN-AP", "Andhra Pradesh"),
    ("IN-AR", "Arunachal Pradesh"),
    ("IN-AS", "Assam"),
    ("IN-BR", "Bihar"),
    ("IN-CT", "Chhattisgarh"),
    ("IN-GA", "Goa"),
    ("IN-GJ", "Gujarat"),
    ("IN-HR", "Haryana"),
    ("IN-HP", "Himachal Pradesh"),
    ("IN-JK", "Jammu and Kashmir"),
    ("IN-JH", "Jharkhand"),
    ("IN-KA", "Karnataka"),
    ("IN-KL", "Kerala"),
    ("IN-MP", "Madhya Pradesh"),
    ("IN-MH", "Maharashtra"),
    ("IN-MN", "Manipur"),
    ("IN-ML", "Meghalaya"),
    ("IN-MZ", "Mizoram"),
    ("IN-NL", "Nagaland"),
    ("IN-OR", "Odisha"),
    ("IN-PB", "Punjab"),
    ("IN-RJ", "Rajasthan"),
    ("IN-SK", "Sikkim"),
    ("IN-TN", "Tamil Nadu"),
    ("IN-TG", "Telangana"),
    ("IN-TR", "Tripura"),
    ("IN-UT", "Uttarakhand"),
    ("IN-UP", "Uttar Pradesh"),
    ("IN-WB", "West Bengal"),
    ("IN-AN", "Andaman and Nicobar Islands"),
    ("IN-CH", "Chandigarh"),
    ("IN-DN", "Dadra and Nagar Haveli"),
    ("IN-DD", "Daman and Diu"),
    ("IN-DL", "Delhi"),
    ("IN-LD", "Lakshadweep"),
    ("IN-PY", "Puducherry")
)


def has_profile(user):
    """ check if user has profile """
    return hasattr(user, 'profile')


def attachments(instance, filename):
    return os.sep.join((instance.workshop_type.name.replace(" ", '_'), filename))


class Profile(models.Model):
    """Profile for users(instructors and coordinators)"""

    user = models.OneToOneField(User, on_delete=models.CASCADE)
    title = models.CharField(max_length=32, blank=True, choices=title)
    institute = models.CharField(max_length=150)
    department = models.CharField(max_length=150, choices=department_choices)
    phone_number = models.CharField(
        max_length=10,
        validators=[RegexValidator(
            regex=r'^.{10}$', message=(
                "Phone number must be entered \
                in the format: '9999999999'.\
                Up to 10 digits allowed.")
        )], null=False)
    position = models.CharField(
            max_length=32, choices=position_choices,
            default='coordinator',
            help_text='Select Coordinator if you want to organise a workshop\
         in your college/school. <br> Select Instructor if you want to conduct\
                 a workshop.')
    how_did_you_hear_about_us = models.CharField(
        max_length=255, blank=True, choices=source
    )
    location = models.CharField(
        max_length=255, blank=True, help_text="Place/City"
    )
    state = models.CharField(max_length=255, choices=states, default="IN-MH")
    is_email_verified = models.BooleanField(default=False)
    activation_key = models.CharField(max_length=255, blank=True, null=True)
    key_expiry_time = models.DateTimeField(blank=True, null=True)

    def __str__(self):
        return f"Profile for {self.user.get_full_name()}"


class WorkshopCategory(models.Model):
    """
    Enhanced workshop categorization
    """
    name = models.CharField(max_length=100)
    description = models.TextField()
    icon = models.CharField(max_length=50, blank=True, help_text="FontAwesome icon class")
    color = models.CharField(max_length=7, default="#007bff", help_text="Hex color code")
    
    def __str__(self):
        return self.name


class WorkshopType(models.Model):
    """"Admin creates types of workshops which can be used by the instructor
        to create workshops.
    """

    name = models.CharField(max_length=120)
    description = models.TextField()
    duration = models.PositiveIntegerField(
        help_text='Please enter duration in days',
        validators=[MinValueValidator(1)]
    )
    terms_and_conditions = models.TextField()
    category = models.ForeignKey(WorkshopCategory, on_delete=models.SET_NULL, null=True, blank=True)
    difficulty_level = models.CharField(max_length=20, choices=[
        ('beginner', 'Beginner'),
        ('intermediate', 'Intermediate'),
        ('advanced', 'Advanced')
    ], default='beginner')
    prerequisites = models.TextField(blank=True)
    learning_outcomes = models.TextField(blank=True)
    materials_provided = models.TextField(blank=True)
    certification_offered = models.BooleanField(default=False)

    def __str__(self):
        return f"{self.name} for {self.duration} day(s)"


class AttachmentFile(models.Model):
    attachments = models.FileField(
        upload_to=attachments, blank=False,
        help_text='Please upload workshop documents one by one, \
                    ie.workshop schedule, instructions etc. \
                    Please Note: Name of Schedule file should be similar to \
                    WorkshopType Name')
    workshop_type = models.ForeignKey(WorkshopType, on_delete=models.CASCADE)


class WorkshopManager(models.Manager):

    def get_workshops_by_state(self, workshops):
        w = workshops.values_list("coordinator__profile__state", flat=True)
        states_map = dict(states)
        df = pd.DataFrame(list(w))
        data_states, data_counts = [], []
        if not df.empty:
            grouped_data = df.value_counts().to_dict()
            for state, count in grouped_data.items():
                state_name = state[0]
                data_states.append(states_map[state_name])
                data_counts.append(count)
        return data_states, data_counts

    def get_workshops_by_type(self, workshops):
        w = workshops.values_list("workshop_type__name", flat=True)
        df = pd.DataFrame(list(w))
        data_wstypes, data_counts = [], []
        if not df.empty:
            grouped_data = df.value_counts().to_dict()
            for ws, count in grouped_data.items():
                ws_name = ws[0]
                data_wstypes.append(ws_name)
                data_counts.append(count)
        return data_wstypes, data_counts


class Workshop(models.Model):
    """
        Contains details of workshops
    """
    uid = models.UUIDField(default=uuid.uuid4, unique=True, editable=False)
    coordinator = models.ForeignKey(User, on_delete=models.CASCADE)
    instructor = models.ForeignKey(
        User, null=True, related_name="%(app_label)s_%(class)s_related",
        on_delete=models.CASCADE
    )
    workshop_type = models.ForeignKey(
        WorkshopType, on_delete=models.CASCADE,
        help_text='Select the type of workshop.'
    )
    date = models.DateField()
    STATUS_CHOICES = [(0, 'Pending'),
                      (1, 'Accepted'),
                      (2, 'Deleted')]

    status = models.IntegerField(choices=STATUS_CHOICES, default=0)
    tnc_accepted = models.BooleanField(
        help_text="I accept the terms and conditions"
    )

    objects = WorkshopManager()

    def __str__(self):
        return f"{self.workshop_type} on {self.date} by {self.coordinator}"

    def get_status(self):
        choice = dict(self.STATUS_CHOICES)
        return choice.get(self.status)


class WorkshopRating(models.Model):
    """
    Workshop ratings and feedback system
    """
    workshop = models.ForeignKey(Workshop, on_delete=models.CASCADE, related_name='ratings')
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    rating = models.IntegerField(choices=[(i, i) for i in range(1, 6)], help_text="Rating from 1-5")
    feedback = models.TextField(blank=True)
    created_date = models.DateTimeField(default=timezone.now)
    
    class Meta:
        unique_together = ('workshop', 'user')
    
    def __str__(self):
        return f"{self.rating}/5 by {self.user.get_full_name()} for {self.workshop}"


class Notification(models.Model):
    """
    Real-time notification system
    """
    NOTIFICATION_TYPES = [
        ('workshop_created', 'Workshop Created'),
        ('workshop_accepted', 'Workshop Accepted'),
        ('workshop_rejected', 'Workshop Rejected'),
        ('workshop_reminder', 'Workshop Reminder'),
        ('new_comment', 'New Comment'),
        ('rating_received', 'Rating Received'),
    ]
    
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='notifications')
    notification_type = models.CharField(max_length=50, choices=NOTIFICATION_TYPES)
    title = models.CharField(max_length=200)
    message = models.TextField()
    related_workshop = models.ForeignKey(Workshop, on_delete=models.CASCADE, null=True, blank=True)
    is_read = models.BooleanField(default=False)
    created_date = models.DateTimeField(default=timezone.now)
    
    class Meta:
        ordering = ['-created_date']
    
    def __str__(self):
        return f"{self.notification_type} for {self.user.get_full_name()}"


class ChatMessage(models.Model):
    """
    Real-time chat system between coordinators and instructors
    """
    sender = models.ForeignKey(User, on_delete=models.CASCADE, related_name='sent_messages')
    receiver = models.ForeignKey(User, on_delete=models.CASCADE, related_name='received_messages')
    workshop = models.ForeignKey(Workshop, on_delete=models.CASCADE, related_name='chat_messages')
    message = models.TextField()
    is_read = models.BooleanField(default=False)
    created_date = models.DateTimeField(default=timezone.now)
    
    class Meta:
        ordering = ['created_date']
    
    def __str__(self):
        return f"Message from {self.sender} to {self.receiver}"


class WorkshopSchedule(models.Model):
    """
    Advanced workshop scheduling with time slots
    """
    workshop = models.OneToOneField(Workshop, on_delete=models.CASCADE, related_name='schedule')
    start_time = models.TimeField()
    end_time = models.TimeField()
    break_duration = models.IntegerField(default=15, help_text="Break duration in minutes")
    max_participants = models.PositiveIntegerField(default=30)
    venue_details = models.TextField(blank=True)
    equipment_required = models.TextField(blank=True)
    
    def __str__(self):
        return f"Schedule for {self.workshop}"


class Testimonial(models.Model):
    """
    Contains Testimonals of Workshops
    """

    name = models.CharField(max_length=150)
    institute = models.CharField(max_length=255)
    department = models.CharField(max_length=150)
    message = models.TextField()

    def __str__(self):
        return f"Testimonial by {self.name}"


class Comment(models.Model):
    """
    Contains comments posted by users on workshop instances
    """

    author = models.ForeignKey(User, on_delete=models.CASCADE)
    comment = models.TextField()
    public = models.BooleanField(default=True)
    created_date = models.DateTimeField(default=timezone.now)
    workshop = models.ForeignKey(Workshop, on_delete=models.CASCADE)

    def __str__(self):
        return f"Comment by {self.author.get_full_name()}"


class Banner(models.Model):
    """
    Add HTML for banner display on homepage
    """
    title = models.CharField(max_length=500)
    html = models.TextField()
    active = models.BooleanField()

    def __str__(self):
        return self.title
